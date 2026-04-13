package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.service.game.thread.annotation.CallerThread;
import com.twb.pokerapp.service.game.thread.dto.PlayerTurnLatchDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowGameInterrupted;
import static com.twb.pokerapp.util.TransactionUtil.afterCommit;

@Slf4j
@RequiredArgsConstructor
public abstract class GameThread extends BaseGameThread implements Thread.UncaughtExceptionHandler {
    // *****************************************************************************************
    // Constants
    // *****************************************************************************************
    private static final int MESSAGE_POLL_DIVISOR = 5;
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

    private int roundCount;

    @Getter
    private PlayerTurnLatchDTO playerTurnLatch;

    @Override
    public void run() {
        initializeThread();
        try {
            initializeTable();

            while (!interruptGame.get()) {
                waitForPlayersToJoin();
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
                    checkTotalRoundsReached();
                }
            }
        } catch (GameInterruptedException e) {
            log.debug("Game interrupted for table {}: {}", table.getId(), e.getMessage());
        } catch (RoundInterruptedException e) {
            log.warn("Round interrupted for table {}: {}", table.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in GameThread for table {}: {}", table.getId(), e.getMessage(), e);
        } finally {
            finishRound();
            finishGame();
        }
    }

    private void initializeThread() {
        setName(params.getTable().getId().toString());
        setPriority(Thread.MAX_PRIORITY);
        setDefaultUncaughtExceptionHandler(this);
        interruptGame.set(false);
        gameInProgress.set(true);
        roundInProgress.set(false);
        roundCount = 0;
        resetDbState();
        params.getStartLatch().countDown();
    }

    private void resetDbState() {
        var tableId = params.getTable().getId();
        writeTx.executeWithoutResult(status -> {
            roundService.reset(tableId);
            bettingRoundService.reset(tableId);
        });
    }

    private void initializeTable() {
        this.table = getThrowGameInterrupted(tableRepository.findById(params.getTable().getId()), "No table found cannot start game");
    }

    private void waitForPlayersToJoin() {
        var pollCount = 0;
        do {
            checkGameInterrupted();
            if (isPlayersJoined()) {
                return;
            }
            if (pollCount % MESSAGE_POLL_DIVISOR == 0) {
                gameLogService.sendLogMessage(table, "Waiting for players to join...");
            }
            gameSpeedService.sleep(params.getDbPollWaitMs());
            pollCount++;
        } while (true);
    }

    private void createNewRound() {
        var roundOpt = roundRepository.findCurrentByTableId(table.getId());
        if (roundOpt.isPresent()) {
            var round = roundOpt.get();
            this.roundId = round.getId();
            if (round.getRoundState() != RoundState.WAITING_FOR_PLAYERS) {
                throw new GameInterruptedException("Cannot start an existing new round not in the WAITING_FOR_PLAYERS state");
            }
        } else {
            writeTx.executeWithoutResult(status -> {
                this.table = getThrowGameInterrupted(tableRepository.findById(table.getId()), "Cannot start as table doesn't exist");
                var connectedPlayers = playerSessionRepository.findConnectedPlayersByTableId(table.getId());
                this.roundId = roundService.create(table, connectedPlayers).getId();
            });
        }
        roundCount++;
        var totalRounds = table.getTotalRounds();
        if (totalRounds != null) {
            gameLogService.sendLogMessage(table, "New Round (%d/%d)...".formatted(roundCount, table.getTotalRounds()));
        } else {
            gameLogService.sendLogMessage(table, "New Round (%d)...".formatted(roundCount));
        }
        gameSpeedService.sleep(params.getRoundStartWaitMs());
    }

    private boolean isPlayersJoined() {
        var minPlayerCount = table.getMinPlayers();
        return readTx.execute(status -> {
            var connectedPlayers = playerSessionRepository
                    .findConnectedByTableId(table.getId());
            var playerPlayerUsers = connectedPlayers.stream()
                    .filter(playerSession -> playerSession.getConnectionType() == ConnectionType.PLAYER)
                    .filter(playerSession -> playerSession.getFunds() != null && playerSession.getFunds().compareTo(BigDecimal.ZERO) > 0)
                    .toList();
            var connectedUsers = userWebsocketService.getConnectedUsers(table);
            if (playerPlayerUsers.isEmpty() && connectedUsers.isEmpty()) {
                throw new GameInterruptedException("No players connected to table so stopping");
            }
            if (playerPlayerUsers.size() < minPlayerCount) {
                log.debug("Waiting for PlayerSessions to connect ({}/{})...", playerPlayerUsers.size(), minPlayerCount);
                return false;
            }
            var websocketUsersCount = connectedPlayers.size();
            if (connectedUsers.size() < websocketUsersCount) {
                log.debug("Waiting for Websocket Users to connect ({}/{})...", connectedUsers.size(), websocketUsersCount);
                return false;
            }
            if (connectedUsers.size() != websocketUsersCount) {
                log.warn("Connected PlayerSessions doesnt equal the connected websocket users ({} vs {})", connectedPlayers.size(), websocketUsersCount);
                return false;
            }
            var connectedUsernames = connectedUsers.stream()
                    .map(SimpUser::getName).toList();
            var connectedPlayerNames = playerPlayerUsers.stream()
                    .map(playerSession -> playerSession.getUser().getUsername()).toList();
            var allPlayersConnected = new HashSet<>(connectedUsernames).containsAll(connectedPlayerNames);
            if (!allPlayersConnected) {
                log.warn("Connected PlayerSessions usernames doesnt equal the connected websocket users usernames ({} vs {})", connectedPlayerNames, connectedUsernames);
                return false;
            }
            log.debug("Connected PlayerSessions match the connected websocket users ({} vs {})", connectedPlayerNames, connectedUsernames);
            return true;
        });
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
                log.debug(e.getMessage());
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
                bettingRoundService.create(table.getId(), bettingRoundType));
    }

    private void checkTotalRoundsReached() {
        var totalRounds = table.getTotalRounds();
        if (totalRounds == null || totalRounds <= 0) {
            return;
        }
        if (roundCount >= totalRounds) {
            throw new GameInterruptedException("Finishing game as total rounds reached: %d/%d".formatted(roundCount, table.getTotalRounds()));
        }
    }

    private void finishRound() {
        if (roundInProgress.compareAndSet(true, false)) {
            writeTx.executeWithoutResult(status -> {
                var roundOpt = roundRepository.findById(roundId);
                roundOpt.ifPresent(round -> {
                    var bettingRoundOpt = bettingRoundRepository.findCurrentByRoundId(roundId);
                    bettingRoundOpt.ifPresent(bettingRound -> {
                        if (bettingRound.getState() != BettingRoundState.FINISHED) {
                            var thisBettingRound = bettingRoundService.setState(bettingRound, BettingRoundState.FINISHED);
                            var roundPots = round.getRoundPots();
                            afterCommit(() -> dispatcher.send(table, messageFactory.bettingRoundUpdated(round, thisBettingRound, roundPots)));
                        }
                    });
                    if (round.getRoundState() != RoundState.FINISHED) {
                        roundService.setState(round, RoundState.FINISHED);
                    }
                    var winners = roundWinnerRepository.findByRound(round.getId());
                    afterCommit(() -> dispatcher.send(table, messageFactory.roundFinished(winners)));
                });
            });
            gameSpeedService.sleep(table, params.getRoundEndWaitMs());
        }
    }

    private void finishGame() {
        if (gameInProgress.compareAndSet(true, false)) {
            if (!userWebsocketService.getConnectedUsers(table).isEmpty()) {
                dispatcher.send(table, messageFactory.gameFinished());
            }
            threadManager.delete(table.getId());
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
        var roundOpt = roundRepository.findCurrentByTableId(table.getId());
        if (roundOpt.isEmpty()) {
            // there is no round so ensure we can move to next one
            interruptRound.set(true);
        } else {
            var round = roundOpt.get();
            var activePlayers = playerSessionRepository.findActivePlayersByTableId(table.getId(), round.getId());
            if (activePlayers.size() < 2) {
                // there is only 1 player left in a started game
                interruptRound.set(true);
            }
        }
        afterCommit(() -> {
            dispatcher.send(table, messageFactory.playerActioned(playerAction));
            if (playerTurnLatch != null) {
                playerTurnLatch.countDown();
                playerTurnLatch = null;
            }
        });
    }

    private void saveRoundState(RoundState roundState) {
        writeTx.executeWithoutResult(transactionStatus -> {
            var roundOpt = roundRepository.findById(roundId);
            roundOpt.ifPresent(round -> roundService.setState(round, roundState));
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
        interruptGame.set(true);
        try {
            var terminated = params.getEndLatch().await(GAME_STOP_TIMEOUT_IN_SECS, TimeUnit.SECONDS);
            if (!terminated) {
                log.warn("Game thread for table {} did not terminate within {} seconds. Forcing interrupt...", table.getId(), GAME_STOP_TIMEOUT_IN_SECS);
                this.interrupt();
            }
        } catch (InterruptedException e) {
            log.error("Failed to wait for game end latch for table {}", table.getId(), e);
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
