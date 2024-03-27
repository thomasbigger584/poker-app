package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.*;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.dto.playeraction.PlayerActionDTO;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.RoundInterruptedException;
import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import com.twb.pokerapp.service.game.DeckOfCardsFactory;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public abstract class GameThread extends BaseGameThread {
    // *****************************************************************************************
    // Constants
    // *****************************************************************************************
    protected static final long DEAL_WAIT_MS = 1000; // 1 second
    protected static final long DB_POLL_WAIT_MS = 1000; // 1 second
    protected static final long EVALUATION_WAIT_MS = 4 * 1000; // 4 seconds
    protected static final long PLAYER_TURN_WAIT_MS = 30 * 1000; // 30 seconds
    private static final Logger logger = LoggerFactory.getLogger(GameThread.class);
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
    protected List<PlayerSession> playerSessions = new ArrayList<>();
    protected final List<PlayerSession> foldedPlayers
            = Collections.synchronizedList(new ArrayList<>());
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
                waitForPlayersToJoinNotZero();
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
            finishRound();
            finishGame();
            logger.error(e.getMessage());
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
        Optional<PokerTable> tableOpt =
                tableRepository.findById(params.getTableId());
        if (tableOpt.isEmpty()) {
            throw new GameInterruptedException("No table found cannot start game");
        }
        pokerTable = tableOpt.get();
    }

    private void waitForPlayersToJoinNotZero() {
        GameType gameType = pokerTable.getGameType();
        int pollCount = 0;
        List<PlayerSession> playerSessions;
        do {
            checkGameInterrupted();
            playerSessions = playerSessionRepository
                    .findConnectedPlayersByTableId(params.getTableId());
            if (playerSessions.size() >= gameType.getMinPlayerCount()) {
                return;
            }
            if (pollCount % MESSAGE_POLL_DIVISOR == 0) {
                sendLogMessage("Waiting for players to join...");
            }
            sleepInMs(DB_POLL_WAIT_MS);
            pollCount++;
        } while (playerSessions.size() < gameType.getMinPlayerCount());
    }

    private void waitForPlayersToJoin(int minPlayerCount) {
        int pollCount = 0;
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
        Optional<Round> roundOpt = roundRepository
                .findCurrentByTableId(params.getTableId());
        if (roundOpt.isPresent()) {
            currentRound = roundOpt.get();
            if (currentRound.getRoundState() != RoundState.WAITING_FOR_PLAYERS) {
                throw new GameInterruptedException("Cannot start an existing new round not in the WAITING_FOR_PLAYERS state");
            }
        } else {
            Optional<PokerTable> tableOpt = tableRepository.findById(params.getTableId());
            if (tableOpt.isEmpty()) {
                throw new GameInterruptedException("Cannot start as table doesn't exist");
            }
            currentRound = roundService.create(pokerTable);
        }
        sendLogMessage("New Round...");
    }

    private boolean isPlayersJoined() {
        GameType gameType = pokerTable.getGameType();
        int minPlayerCount = gameType.getMinPlayerCount();
        return isPlayersJoined(minPlayerCount);
    }

    private boolean isPlayersJoined(int count) {
        List<PlayerSession> playerSessions = getPlayerSessionsNotZero();
        return playerSessions.size() >= count;
    }

    protected List<PlayerSession> getPlayerSessionsNotZero() {
        List<PlayerSession> playerSessions = playerSessionRepository
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
        onInitRound();
    }

    private void runRound() {
        RoundState roundState = RoundState.INIT_DEAL;
        saveRoundState(roundState);
        while (roundState != RoundState.FINISH) {
            checkRoundInterrupted();
            try {
                onRunRound(roundState);
                roundState = getNextRoundState(roundState);
            } catch (RoundInterruptedException e) {
                if (roundState != RoundState.EVAL) {
                    roundState = RoundState.EVAL;
                }
            }
            saveRoundState(roundState);
        }
    }

    protected void shuffleCards() {
        deckOfCards = DeckOfCardsFactory.getCards(true);
        deckCardPointer = 0;
    }

    protected Card getCard() {
        Card card = new Card(deckOfCards.get(deckCardPointer));
        deckCardPointer++;
        return card;
    }

    protected void saveRoundState(RoundState roundState) {
        currentRound.setRoundState(roundState);
        roundRepository.saveAndFlush(currentRound);
    }

    protected void waitPlayerTurn(PlayerSession playerSession) {
        playerTurnLatch = new CountDownLatch(1);
        try {
            boolean await = playerTurnLatch.await(PLAYER_TURN_WAIT_MS, TimeUnit.MILLISECONDS);
            if (!await) {
                CreatePlayerActionDTO createPlayerActionDTO = new CreatePlayerActionDTO();
                createPlayerActionDTO.setAction(ActionType.FOLD);
                playerAction(playerSession, createPlayerActionDTO);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to wait for player turn latch", e);
        }
    }

    public void playerAction(String username, CreatePlayerActionDTO createDto) {
        logger.info("***************************************************************");
        logger.info("GameThread.playerAction");
        logger.info("username = {}, action = {}", username, createDto);
        logger.info("***************************************************************");
        Optional<PlayerSession> playerSessionOpt = playerSessionRepository
                .findByTableIdAndUsername(pokerTable.getId(), username);
        if (playerSessionOpt.isEmpty()) {
            logger.warn("No player {} found on table {}", username, pokerTable.getId());
            return;
        }
        playerAction(playerSessionOpt.get(), createDto);
    }

    private void playerAction(PlayerSession playerSession, CreatePlayerActionDTO createDto) {
        PlayerActionDTO actionDto = playerActionService.create(playerSession, currentRound, createDto);
        switch (createDto.getAction()) {
            case FOLD -> fold(playerSession);
            //todo: add others
        }
        dispatcher.send(params.getTableId(), messageFactory.playerAction(actionDto));
        playerTurnLatch.countDown();
    }

    private void finishRound() {
        if (roundInProgress.get()) {
            saveRoundState(RoundState.FINISH);
            dispatcher.send(params.getTableId(), messageFactory.roundFinished());
        }
        roundInProgress.set(false);
    }

    protected boolean isPlayerFolded(PlayerSession playerSession) {
        return foldedPlayers.contains(playerSession);
    }

    // NOTE: called from both game thread and main thread
    private void fold(PlayerSession playerSession) {
        synchronized (foldedPlayers) {
            if (!foldedPlayers.contains(playerSession)) {
                foldedPlayers.add(playerSession);
            }
            if (playerSessions.stream()
                    .filter(foldedPlayers::contains).count() == 1) {
                // there is only 1 player left in a started game
                interruptRound.set(true);
            }
        }
    }

    private void finishGame() {
        if (gameInProgress.get()) {
            dispatcher.send(params.getTableId(), messageFactory.gameFinished());
            threadManager.delete(params.getTableId());
        }
        gameInProgress.set(false);
    }

    // *****************************************************************************************
    // Logging Dispatch Methods
    // *****************************************************************************************

    protected void sendLogMessage(String message) {
        dispatcher.send(params.getTableId(), messageFactory.logMessage(message));
    }

    protected void sendErrorMessage(String message) {
        dispatcher.send(params.getTableId(), messageFactory.errorMessage(message));
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

    public void stopThread() {
        interruptGame.set(true);
        interrupt();
    }

    // *****************************************************************************************
    // Evaluation
    // *****************************************************************************************

    protected void handleWinners(List<EvalPlayerHandDTO> winners) {
        if (winners.size() == 1) {
            handleSinglePlayerWin(winners.get(0));
        } else {
            handleMultiplePlayerWin(winners);
        }
    }

    private void handleSinglePlayerWin(EvalPlayerHandDTO winningPlayerHandDTO) {
        PlayerSession playerSession = winningPlayerHandDTO.getPlayerSession();
        String username = playerSession.getUser().getUsername();
        String handTypeStr = winningPlayerHandDTO.getHandType().getValue();

        sendLogMessage(String.format("%s wins round with a %s", username, handTypeStr));
    }

    private void handleMultiplePlayerWin(List<EvalPlayerHandDTO> winners) {
        String winnerNames = getReadableWinners(winners);
        String handTypeStr = winners.get(0).getHandType().getValue();

        sendLogMessage(String.format("%s draws round with a %s", winnerNames, handTypeStr));
    }

    private String getReadableWinners(List<EvalPlayerHandDTO> winners) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < winners.size(); index++) {
            EvalPlayerHandDTO eval = winners.get(index);
            AppUser user = eval.getPlayerSession().getUser();
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
    // Abstract Methods
    // ***************************************************************

    abstract protected void onInitRound();

    abstract protected void onRunRound(RoundState roundState);

    abstract protected RoundState getNextRoundState(RoundState roundState);
}
