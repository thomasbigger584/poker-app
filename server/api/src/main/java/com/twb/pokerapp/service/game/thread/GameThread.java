package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.service.game.DeckOfCardsFactory;
import com.twb.pokerapp.service.game.thread.annotation.CallerThread;
import com.twb.pokerapp.service.game.thread.dto.PlayerTurnLatchDTO;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowGameInterrupted;
import static com.twb.pokerapp.service.game.thread.util.SleepUtil.sleepInMs;

@Slf4j
@RequiredArgsConstructor
public abstract class GameThread extends BaseGameThread implements Thread.UncaughtExceptionHandler {
    // *****************************************************************************************
    // Constants
    // *****************************************************************************************
    private static final int MESSAGE_POLL_DIVISOR = 5;
    private static final int MINIMUM_PLAYERS_CONNECTED = 1;

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
    protected UUID roundId;

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
        }
    }

    private void initializeThread() {
        setName(params.getTableId().toString());
        setPriority(Thread.MAX_PRIORITY);
        setDefaultUncaughtExceptionHandler(this);
        interruptGame.set(false);
        gameInProgress.set(true);
        roundInProgress.set(false);
        params.getStartLatch().countDown();
    }

    private void initializeTable() {
        this.table = getThrowGameInterrupted(tableRepository.findById(params.getTableId()), "No table found cannot start game");
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
            playerSessions = playerSessionRepository.findConnectedPlayersByTableId(params.getTableId());
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
        var roundOpt = roundRepository.findCurrentByTableId(params.getTableId());
        if (roundOpt.isPresent()) {
            var round = roundOpt.get();
            this.roundId = round.getId();
            if (round.getRoundState() != RoundState.WAITING_FOR_PLAYERS) {
                throw new GameInterruptedException("Cannot start an existing new round not in the WAITING_FOR_PLAYERS state");
            }
        } else {
            writeTx.executeWithoutResult(status -> {
                var table = getThrowGameInterrupted(tableRepository.findById(params.getTableId()), "Cannot start as table doesn't exist");
                this.roundId = roundService.create(table).getId();
            });
        }
        gameLogService.sendLogMessage(table, "New Round...");
        sleepInMs(params.getRoundStartWaitMs());
    }

    private boolean isPlayersJoined() {
        var minPlayers = table.getGameType().getMinPlayerCount();
        return isPlayersJoined(minPlayers);
    }

    private boolean isPlayersJoined(int count) {
        var playerSessions = playerSessionRepository.findConnectedPlayersByTableId(params.getTableId());
        if (CollectionUtils.isEmpty(playerSessions)) {
            throw new GameInterruptedException("No more players connected");
        }
        return playerSessions.size() >= count;
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
        var bettingRoundTypeOpt = roundState.getBettingRoundType();
        bettingRoundTypeOpt.ifPresent(bettingRoundType -> bettingRoundService.create(params.getTableId(), bettingRoundType));
    }

    private void finishRound() {
        if (roundInProgress.get()) {
            writeTx.executeWithoutResult(status -> {
                var bettingRoundOpt = bettingRoundRepository.findCurrentByRoundId(roundId);
                bettingRoundOpt.ifPresent(bettingRound ->
                        bettingRoundService.setBettingRoundFinished(bettingRound));

                var roundOpt = roundRepository.findById(roundId);
                roundOpt.ifPresent(round ->
                        roundService.setRoundState(round, RoundState.FINISHED));

                dispatcher.send(table, messageFactory.roundFinished());
            });
        }
        roundInProgress.set(false);
        sleepInMs(params.getRoundEndWaitMs());
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

    @CallerThread
    @Transactional(propagation = Propagation.MANDATORY)
    public void onPostPlayerAction(CreatePlayerActionDTO createActionDto) {
        if (playerTurnLatch != null) {
            playerTurnLatch.countDown();
            playerTurnLatch = null;
        }
        var roundOpt = roundRepository.findCurrentByTableId(params.getTableId());
        if (roundOpt.isEmpty()) {
            // there is no round so ensure we can move to next one
            interruptRound.set(true);
        } else {
            var round = roundOpt.get();
            var activePlayers = playerSessionRepository.findActivePlayersByTableId(params.getTableId(), round.getId());
            if (activePlayers.size() < 2) {
                // there is only 1 player left in a started game
                interruptRound.set(true);
            }
        }
    }

    private void saveRoundState(RoundState roundState) {
        writeTx.executeWithoutResult(transactionStatus -> {
            var roundOpt = roundRepository.findById(roundId);
            roundOpt.ifPresent(round -> roundService.setRoundState(round, roundState));
        });
    }

    private void checkGameInterrupted() {
        if (interruptGame.get() || Thread.interrupted()) {
            var endLatch = params.getEndLatch();
            for (var index = 0; index < endLatch.getCount(); index++) {
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

    @CallerThread
    public void stopGame() {
        interruptGame.set(true);
        try {
            params.getEndLatch().await();
        } catch (InterruptedException e) {
            log.error("Failed to wait for game end latch", e);
        }
    }

    // ***************************************************************
    // Lifecycle Methods
    // ***************************************************************

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error("Exception thrown in thread", throwable);
        finishRound();
        finishGame();
        stopGame();
    }

    // ***************************************************************
    // Abstract Methods
    // ***************************************************************

    protected abstract void onInitRound();

    protected abstract void onRunRound(RoundState roundState);

    protected abstract RoundState getNextRoundState(RoundState roundState);
}
