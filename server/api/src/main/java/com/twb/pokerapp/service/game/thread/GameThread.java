package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.*;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import com.twb.pokerapp.service.game.DeckOfCardsFactory;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public abstract class GameThread extends BaseGameThread {
    // *****************************************************************************************
    // Constants
    // *****************************************************************************************
    protected static final long DEAL_WAIT_MS = 1000; // 1 second
    protected static final long DB_POLL_WAIT_MS = 1000; // 1 second
    protected static final long EVALUATION_WAIT_MS = 4 * 1000; // 4 seconds
    protected static final long PLAYER_TURN_WAIT_MS = 30 * 1000; // 30 seconds
    private static final int MESSAGE_POLL_DIVISOR = 5;
    private static final int MINIMUM_PLAYERS_CONNECTED = 1;
    private static final String NO_MORE_PLAYERS_CONNECTED = "No more players connected";

    // *****************************************************************************************
    // Constructor Fields
    // *****************************************************************************************
    protected final GameThreadParams params;

    // *****************************************************************************************
    // Flags
    // *****************************************************************************************
    private final AtomicBoolean interruptGame = new AtomicBoolean(false);
    private final AtomicBoolean interruptRound = new AtomicBoolean(false);
    private final AtomicBoolean roundInProgress = new AtomicBoolean(false);
    private final AtomicBoolean gameInProgress = new AtomicBoolean(false);

    // *****************************************************************************************
    // Fields
    // *****************************************************************************************
    protected PokerTable pokerTable;
    protected Round currentRound;
    protected BettingRound currentBettingRound;
    protected List<PlayerSession> playerSessions = new ArrayList<>();
    protected final Set<PlayerSession> foldedPlayers = Collections.synchronizedSet(new HashSet<>());
    private CountDownLatch playerTurnLatch;
    private List<Card> deckOfCards;
    private int deckCardPointer;

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
                    checkGameInterrupted();
                    initRound();
                    checkGameInterrupted();
                    runRound();
                    checkGameInterrupted();
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
                sendErrorMessage(e.getMessage());
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
        var tableOpt = tableRepository.findById(params.getTableId());
        if (tableOpt.isEmpty()) {
            throw new GameInterruptedException("No table found cannot start game");
        }
        pokerTable = tableOpt.get();
    }

    private void waitForMinimumPlayersToJoin() {
        var gameType = pokerTable.getGameType();
        waitForPlayersToJoin(gameType.getMinPlayerCount());
    }

    private void waitForPlayersToJoin(int minPlayerCount) {
        var pollCount = 0;
        List<PlayerSession> playerSessions;
        do {
            checkGameInterrupted();
            playerSessions = playerSessionRepository
                    .findConnectedPlayersByTableId(params.getTableId());
            if (playerSessions.size() >= minPlayerCount) {
                return;
            }
            if (pollCount % MESSAGE_POLL_DIVISOR == 0) {
                sendLogMessage("Waiting for players to join...");
            }
            sleepInMs(DB_POLL_WAIT_MS);
            pollCount++;
        } while (playerSessions.size() < minPlayerCount);
    }

    private void createNewRound() {
        var roundOpt = roundRepository.findCurrentByTableId(params.getTableId());
        if (roundOpt.isPresent()) {
            currentRound = roundOpt.get();
            if (currentRound.getRoundState() != RoundState.WAITING_FOR_PLAYERS) {
                throw new GameInterruptedException("Cannot start an existing new round not in the WAITING_FOR_PLAYERS state");
            }
        } else {
            var tableOpt = tableRepository.findById(params.getTableId());
            if (tableOpt.isEmpty()) {
                throw new GameInterruptedException("Cannot start as table doesn't exist");
            }
            currentRound = roundService.create(pokerTable);
        }
        sendLogMessage("New Round...");
    }

    private boolean isPlayersJoined() {
        var gameType = pokerTable.getGameType();
        var minPlayerCount = gameType.getMinPlayerCount();
        return isPlayersJoined(minPlayerCount);
    }

    private boolean isPlayersJoined(int count) {
        var playerSessions = getPlayerSessionsNotZero();
        return playerSessions.size() >= count;
    }

    protected List<PlayerSession> getPlayerSessionsNotZero() {
        var playerSessions = playerSessionRepository
                .findConnectedPlayersByTableId(params.getTableId());
        if (CollectionUtils.isEmpty(playerSessions)) {
            throw new GameInterruptedException(NO_MORE_PLAYERS_CONNECTED);
        }
        return playerSessions;
    }

    private void initRound() {
        roundInProgress.set(true);
        interruptRound.set(false);
        foldedPlayers.clear();
        shuffleCards();
        checkRoundInterrupted();
        onInitRound();
    }

    private void runRound() {
        var roundState = RoundState.INIT_DEAL;
        saveRoundState(roundState);
        while (roundState != RoundState.FINISH) {
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
        var bettingRoundStateOpt = Optional.ofNullable(roundState.getBettingRoundState());
        if (bettingRoundStateOpt.isEmpty()) {
            currentBettingRound = null;
            return;
        }
        currentBettingRound = bettingRoundService.create(currentRound, bettingRoundStateOpt.get());
    }

    private void finishRound() {
        if (roundInProgress.get()) {
            saveRoundState(RoundState.FINISH);
            dispatcher.send(pokerTable, messageFactory.roundFinished());
        }
        roundInProgress.set(false);
    }

    private void finishGame() {
        if (gameInProgress.get()) {
            dispatcher.send(pokerTable, messageFactory.gameFinished());
            threadManager.delete(params.getTableId());
        }
        gameInProgress.set(false);
    }

    // *****************************************************************************************
    // Player Action Methods
    // *****************************************************************************************

    public void playerAction(String username, CreatePlayerActionDTO createDto) {
        log.info("***************************************************************");
        log.info("GameThread.playerAction");
        log.info("username = {}, action = {}", username, createDto);
        log.info("***************************************************************");
        var playerSessionOpt = playerSessionRepository
                .findByTableIdAndUsername(pokerTable.getId(), username);
        if (playerSessionOpt.isEmpty()) {
            log.warn("No player {} found on table {}", username, pokerTable.getId());
            return;
        }
        playerAction(playerSessionOpt.get(), createDto);
    }

    private void playerAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        //noinspection SwitchStatementWithTooFewBranches
        var actioned = switch (createActionDto.getAction()) {
            case FOLD -> foldAction(playerSession, createActionDto);
            // todo: add more generic actions
            default ->  onPlayerAction(playerSession, createActionDto);
        };
        if (actioned) {
            playerTurnLatch.countDown();
        }
    }

    // NOTE: called from both game thread and main thread
    private boolean foldAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        synchronized (foldedPlayers) {
            foldedPlayers.add(playerSession);

            createActionDto.setAmount(0d);
            playerActionService.create(playerSession, currentBettingRound, createActionDto);

            if (playerSessions.stream().filter(foldedPlayers::contains).count() == 1) {
                // there is only 1 player left in a started game
                interruptRound.set(true);
            }
        }
        return true;
    }

    // *****************************************************************************************
    // Logging Dispatch Methods
    // *****************************************************************************************

    protected void sendLogMessage(String message) {
        dispatcher.send(pokerTable, messageFactory.logMessage(message));
    }

    protected void sendErrorMessage(String message) {
        dispatcher.send(pokerTable, messageFactory.errorMessage(message));
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

    private void saveRoundState(RoundState roundState) {
        currentRound.setRoundState(roundState);
        roundRepository.saveAndFlush(currentRound);
    }

    protected void waitPlayerTurn(PlayerSession playerSession) {
        playerTurnLatch = new CountDownLatch(1);
        try {
            var await = playerTurnLatch.await(PLAYER_TURN_WAIT_MS, TimeUnit.MILLISECONDS);
            if (!await) {
                var createPlayerActionDTO = new CreatePlayerActionDTO();
                createPlayerActionDTO.setAction(ActionType.FOLD);
                playerAction(playerSession, createPlayerActionDTO);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to wait for player turn latch", e);
        }
    }

    protected boolean isPlayerFolded(PlayerSession playerSession) {
        return foldedPlayers.contains(playerSession);
    }

    // *****************************************************************************************
    // Thread Utility Methods
    // *****************************************************************************************

    protected void sleepInMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to sleep for " + ms + "ms", e);
        }
    }

    protected void checkGameInterrupted() {
        if (interruptGame.get() || isInterrupted()) {
            throw new GameInterruptedException("Game is interrupted");
        }
    }

    protected void checkRoundInterrupted() {
        checkGameInterrupted();
        if (interruptRound.get()) {
            throw new RoundInterruptedException("Round is interrupted");
        }
    }

    // *****************************************************************************************
    // Evaluation
    // *****************************************************************************************

    protected void handleWinners(List<EvalPlayerHandDTO> winners) {
        if (winners.size() == 1) {
            handleSinglePlayerWin(winners.getFirst());
        } else {
            handleMultiplePlayerWin(winners);
        }
    }

    private void handleSinglePlayerWin(EvalPlayerHandDTO winningPlayerHandDTO) {
        var playerSession = winningPlayerHandDTO.getPlayerSession();
        var username = playerSession.getUser().getUsername();
        var handTypeStr = winningPlayerHandDTO.getHandType().getValue();

        sendLogMessage(String.format("%s wins round with a %s", username, handTypeStr));
    }

    private void handleMultiplePlayerWin(List<EvalPlayerHandDTO> winners) {
        var winnerNames = getReadableWinners(winners);
        var handTypeStr = winners.getFirst().getHandType().getValue();

        sendLogMessage(String.format("%s draws round with a %s", winnerNames, handTypeStr));
    }

    private String getReadableWinners(List<EvalPlayerHandDTO> winners) {
        var sb = new StringBuilder();
        for (var index = 0; index < winners.size(); index++) {
            var eval = winners.get(index);
            var user = eval.getPlayerSession().getUser();
            sb.append(user.getUsername());
            if (index < winners.size() - 3) {
                sb.append(", ");
            } else if (index == winners.size() - 2) {
                sb.append(" & ");
            }
        }
        return sb.toString();
    }

    // ***************************************************************
    // Lifecycle Methods
    // ***************************************************************

    @Override
    public void interrupt() {
        interruptGame.set(true);
        super.interrupt();
    }

    // ***************************************************************
    // Abstract Methods
    // ***************************************************************

    protected abstract void onInitRound();

    protected abstract void onRunRound(RoundState roundState);

    protected abstract boolean onPlayerAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto);

    protected abstract RoundState getNextRoundState(RoundState roundState);
}
