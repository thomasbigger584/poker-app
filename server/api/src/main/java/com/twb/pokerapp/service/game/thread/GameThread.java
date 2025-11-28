package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.*;
import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.service.game.DeckOfCardsFactory;
import com.twb.pokerapp.service.game.thread.dto.PlayerTurnLatchDTO;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.twb.pokerapp.service.game.thread.util.SleepUtil.sleepInMs;

@Slf4j
@RequiredArgsConstructor
public abstract class GameThread extends BaseGameThread {
    // *****************************************************************************************
    // Constants
    // *****************************************************************************************
    private static final int MESSAGE_POLL_DIVISOR = 5;
    private static final int MINIMUM_PLAYERS_CONNECTED = 1;
    private static final String NO_MORE_PLAYERS_CONNECTED = "No more players connected";

    // *****************************************************************************************
    // Constructor Fields
    // *****************************************************************************************
    protected final GameThreadParams params;

    // *****************************************************************************************
    // Fields
    // *****************************************************************************************
    private final AtomicBoolean interruptGame = new AtomicBoolean(false);
    private final AtomicBoolean interruptRound = new AtomicBoolean(false);
    private final AtomicBoolean roundInProgress = new AtomicBoolean(false);
    private final AtomicBoolean gameInProgress = new AtomicBoolean(false);

    protected PokerTable table;
    protected Round round;
    protected BettingRound bettingRound;

    private List<Card> deckOfCards;
    private int deckCardPointer;

    @Getter
    private PlayerTurnLatchDTO playerTurnLatch;

    @Override
    public void run() {
        initializeThread();
        try {
            initializeTable();

            waitForPlayersToJoin(MINIMUM_PLAYERS_CONNECTED);

            while (isPlayersJoined(MINIMUM_PLAYERS_CONNECTED)) {
                checkGameInterrupted();
                waitForMinimumPlayersToJoin();
                checkGameInterrupted();
                while (isPlayersJoined()) {
                    checkGameInterrupted();
                    createNewRound();
                    checkRoundInterrupted();
                    initRound();
                    checkRoundInterrupted();
                    runRound();
                    checkRoundInterrupted();
                    finishRound();
                    checkGameInterrupted();
                }
            }
            finishGame();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            finishRound();
            finishGame();
            if (e instanceof GameInterruptedException) {
                gameLogService.sendErrorMessage(table, e.getMessage());
            }
        }
    }

    private void initializeThread() {
        setName(params.getTableId().toString());
        setPriority(Thread.MAX_PRIORITY);
        interruptGame.set(false);
        gameInProgress.set(true);
        roundInProgress.set(false);
        params.getStartLatch().countDown();
    }

    private void initializeTable() {
        var tableOpt = tableRepository.findById_Lock(params.getTableId());
        if (tableOpt.isEmpty()) {
            throw new GameInterruptedException("No table found cannot start game");
        }
        table = tableOpt.get();
    }

    private void waitForMinimumPlayersToJoin() {
        var minPlayers = table.getGameType().getMinPlayerCount();
        waitForPlayersToJoin(minPlayers);
    }

    private void waitForPlayersToJoin(int minPlayerCount) {
        var pollCount = 0;
        List<PlayerSession> playerSessions;
        do {
            checkGameInterrupted();
            playerSessions = playerSessionRepository
                    .findConnectedPlayersByTableId_Lock(params.getTableId());
            if (playerSessions.size() >= minPlayerCount) {
                return;
            }
            if (pollCount % MESSAGE_POLL_DIVISOR == 0) {
                gameLogService.sendLogMessage(table, "Waiting for players to join...");
            }
            sleepInMs(params.getDbPollWaitMs());
            pollCount++;
        } while (playerSessions.size() < minPlayerCount);
    }

    private void createNewRound() {
        var roundOpt = roundRepository.findCurrentByTableId_Lock(params.getTableId());
        if (roundOpt.isPresent()) {
            this.round = roundOpt.get();
            if (round.getRoundState() != RoundState.WAITING_FOR_PLAYERS) {
                throw new GameInterruptedException("Cannot start an existing new round not in the WAITING_FOR_PLAYERS state");
            }

        } else {
            var tableOpt = tableRepository.findById_Lock(params.getTableId());
            if (tableOpt.isEmpty()) {
                throw new GameInterruptedException("Cannot start as table doesn't exist");
            }
            this.round = roundService.create(table);
        }
        gameLogService.sendLogMessage(table, "New Round...");
    }

    private boolean isPlayersJoined() {
        var minPlayers = table.getGameType().getMinPlayerCount();
        return isPlayersJoined(minPlayers);
    }

    private boolean isPlayersJoined(int count) {
        var playerSessions = getPlayerSessionsNotZero();
        return playerSessions.size() >= count;
    }

    protected List<PlayerSession> getPlayerSessionsNotZero() {
        var playerSessions = playerSessionRepository
                .findConnectedPlayersByTableId_Lock(params.getTableId());
        if (CollectionUtils.isEmpty(playerSessions)) {
            throw new GameInterruptedException(NO_MORE_PLAYERS_CONNECTED);
        }
        return playerSessions;
    }

    private void initRound() {
        roundInProgress.set(true);
        interruptRound.set(false);
        shuffleCards();
        checkRoundInterrupted();
        onInitRound();
    }

    private void runRound() {
        var roundState = RoundState.INIT_DEAL;
        saveRoundState(roundState);
        while (roundState != RoundState.FINISHED) {
            checkRoundInterrupted();
            try {
                initBettingRound(roundState);
                onRunRound(roundState);
                roundState = getNextRoundState(roundState);
            } catch (RoundInterruptedException e) {
                log.info(e.getMessage());
                interruptRound.set(false);
                if (roundState != RoundState.EVAL) {
                    roundState = RoundState.EVAL;
                }
            }
            saveRoundState(roundState);
        }
    }

    private void initBettingRound(RoundState roundState) {
        var bettingRoundStateOpt = Optional.ofNullable(roundState.getBettingRoundType());
        if (bettingRoundStateOpt.isEmpty()) {
            bettingRound = null;
            return;
        }
        bettingRound = bettingRoundService.create(round, bettingRoundStateOpt.get());
    }

    private void finishRound() {
        if (roundInProgress.get()) {
            finishBettingRoundState();
            saveRoundState(RoundState.FINISHED);
            dispatcher.send(table, messageFactory.roundFinished());
        }
        roundInProgress.set(false);
        sleepInMs(params.getRoundEndWaitMs());
    }

    private void finishBettingRoundState() {
        if (bettingRound != null) {
            bettingRound.setState(BettingRoundState.FINISHED);
            bettingRoundRepository.saveAndFlush(bettingRound);
        }
    }

    private void finishGame() {
        if (gameInProgress.get()) {
            dispatcher.send(table, messageFactory.gameFinished());
            threadManager.delete(params.getTableId());
        }
        gameInProgress.set(false);
    }

    // *****************************************************************************************
    // Game Utility Methods
    // *****************************************************************************************

    protected void shuffleCards() {
        deckOfCards = DeckOfCardsFactory.getCards(true);
        deckCardPointer = 0;
    }

    protected Card getCard() {
        var card = new Card(deckOfCards.get(deckCardPointer));
        deckCardPointer++;
        return card;
    }

    public PlayerTurnLatchDTO newPlayerTurnLatch(PlayerSession turnPlayerSession) {
        playerTurnLatch = PlayerTurnLatchDTO.of(turnPlayerSession);
        return playerTurnLatch;
    }

    public void onPostPlayerAction(CreatePlayerActionDTO createActionDto) {
        if (playerTurnLatch != null) {
            playerTurnLatch.countDown();
        }
        var activePlayers = playerSessionRepository
                .findActivePlayersByTableId_Lock(table.getId(), round.getId());
        if (activePlayers.size() < 2) {
            // there is only 1 player left in a started game
            interruptRound.set(true);
        }
    }

    private void saveRoundState(RoundState roundState) {
        round.setRoundState(roundState);
        roundRepository.saveAndFlush(round);
    }

    public void checkGameInterrupted() {
        if (interruptGame.get() || Thread.interrupted()) {
            var endLatch = params.getEndLatch();
            if (endLatch.getCount() > 0) {
                endLatch.countDown();
            }
            throw new GameInterruptedException("Game is interrupted");
        }
    }

    public void checkRoundInterrupted() {
        checkGameInterrupted();
        if (interruptRound.get()) {
            throw new RoundInterruptedException("Round is interrupted");
        }
    }

    // called from outside thread
    public void stopGame() {
        interruptGame.set(true);
        try {
            params.getEndLatch().await();
        } catch (InterruptedException e) {
            log.error("Failed to wait for game end latch", e);
        }
        System.out.println();
    }

    // ***************************************************************
    // Abstract Methods
    // ***************************************************************

    protected abstract void onInitRound();

    protected abstract void onRunRound(RoundState roundState);

    protected abstract RoundState getNextRoundState(RoundState roundState);
}
