package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.service.game.thread.annotation.CallerThread;
import com.twb.pokerapp.service.game.thread.dto.PlayerTurnLatchDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowGameInterrupted;
import static com.twb.pokerapp.util.SleepUtil.sleepInMs;
import static com.twb.pokerapp.util.TransactionUtil.afterCommit;

@Slf4j
@RequiredArgsConstructor
public abstract class GameThread extends BaseGameThread implements Thread.UncaughtExceptionHandler {
    // *****************************************************************************************
    // Constants
    // *****************************************************************************************
    private static final int MESSAGE_POLL_DIVISOR = 5;
    private static final int MINIMUM_PLAYERS_CONNECTED = 1;
    private static final int GAME_STOP_TIMEOUT_IN_SECS = 10;

    // *****************************************************************************************
    // Constructor Fields
    // *****************************************************************************************
    @Getter
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

            while (!interruptGame.get()) {
                waitForPlayersToJoin(MINIMUM_PLAYERS_CONNECTED);
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
        } catch (GameInterruptedException | RoundInterruptedException e) {
            log.info("Game or Round interrupted for table {}: {}", params.getTableId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in GameThread for table {}: {}", params.getTableId(), e.getMessage(), e);
        } finally {
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
        do {
            checkGameInterrupted();
            if (isPlayersJoined(minPlayerCount)) {
                return;
            }
            if (pollCount % MESSAGE_POLL_DIVISOR == 0) {
                gameLogService.sendLogMessage(table, "Waiting for players to join...");
            }
            sleepInMs(params.getDbPollWaitMs());
            pollCount++;
        } while (true);
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
                this.table = getThrowGameInterrupted(tableRepository.findById(params.getTableId()), "Cannot start as table doesn't exist");
                var connectedPlayers = playerSessionRepository.findConnectedPlayersByTableId(params.getTableId());
                this.roundId = roundService.create(table, connectedPlayers).getId();
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
        return playerSessionRepository.countConnectedPlayersByTableId(params.getTableId()) >= count;
    }

    private void initRound() {
        if (roundInProgress.compareAndSet(false, true)) {
            interruptRound.set(false);
            shuffleCards();
            checkRoundInterrupted();
            onInitRound();
        }
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
        bettingRoundTypeOpt.ifPresent(bettingRoundType ->
                bettingRoundService.create(params.getTableId(), bettingRoundType));
    }

    private void finishRound() {
        if (roundInProgress.compareAndSet(true, false)) {
            writeTx.executeWithoutResult(status -> {
                var roundOpt = roundRepository.findById(roundId);
                roundOpt.ifPresent(round -> {
                    var bettingRoundOpt = bettingRoundRepository.findCurrentByRoundId(roundId);
                    bettingRoundOpt.ifPresent(bettingRound -> {
                        if (bettingRound.getState() != BettingRoundState.FINISHED) {
                            var thisBettingRound = bettingRoundService.setBettingRoundFinished(bettingRound);
                            var roundPots = round.getRoundPots();
                            afterCommit(() -> dispatcher.send(params, messageFactory.bettingRoundUpdated(round, thisBettingRound, roundPots)));
                        }
                    });
                    if (round.getRoundState() != RoundState.FINISHED) {
                        roundService.setRoundState(round, RoundState.FINISHED);
                    }
                    var winners = roundWinnerRepository.findByRound(round.getId());
                    afterCommit(() -> dispatcher.send(table, messageFactory.roundFinished(winners)));
                });
            });
            sleepInMs(params.getRoundEndWaitMs());
        }
    }

    private void finishGame() {
        if (gameInProgress.compareAndSet(true, false)) {
            dispatcher.send(table, messageFactory.gameFinished());
            threadManager.delete(params.getTableId());
        }
    }

    // *****************************************************************************************
    // Game Utility Methods
    // *****************************************************************************************

    protected void shuffleCards() {
        deckOfCards = deckFactory.getShuffledDeck();
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
    public void onPostPlayerAction(PlayerAction playerAction) {
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
        afterCommit(() -> {
            dispatcher.send(params, messageFactory.playerActioned(playerAction));
            if (playerTurnLatch != null) {
                playerTurnLatch.countDown();
                playerTurnLatch = null;
            }
        });
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
            while (endLatch.getCount() > 0) {
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
        interruptGame.compareAndSet(false, true);
        try {
            var terminated = params.getEndLatch().await(GAME_STOP_TIMEOUT_IN_SECS, TimeUnit.SECONDS);
            if (!terminated) {
                log.warn("Game thread for table {} did not terminate within {} seconds. Forcing interrupt...", params.getTableId(), GAME_STOP_TIMEOUT_IN_SECS);
                this.interrupt();
            }
        } catch (InterruptedException e) {
            log.error("Failed to wait for game end latch for table {}", params.getTableId(), e);
            Thread.currentThread().interrupt();
        }
    }

    public boolean isStopping() {
        return interruptGame.get()
                || !gameInProgress.get();
    }

    // ***************************************************************
    // Lifecycle Methods
    // ***************************************************************

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error("Exception thrown in thread", throwable);
        stopGame();
    }

    // ***************************************************************
    // Abstract Methods
    // ***************************************************************

    protected abstract void onInitRound();

    protected abstract void onRunRound(RoundState roundState);

    protected abstract RoundState getNextRoundState(RoundState roundState);
}
