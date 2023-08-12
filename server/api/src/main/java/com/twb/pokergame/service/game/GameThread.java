package com.twb.pokergame.service.game;

import com.twb.pokergame.domain.*;
import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.repository.*;
import com.twb.pokergame.service.CardService;
import com.twb.pokergame.service.HandService;
import com.twb.pokergame.service.RoundService;
import com.twb.pokergame.service.eval.HandEvaluator;
import com.twb.pokergame.service.eval.dto.EvalPlayerHandDTO;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public abstract class GameThread extends Thread {
    protected static final SecureRandom RANDOM = new SecureRandom();
    protected static final int DEAL_WAIT_MS = 1000;
    protected static final int DB_POLL_WAIT_MS = 1000;
    protected static final int EVALUATION_WAIT_MS = 4000;
    private static final int MINIMUM_PLAYERS_CONNECTED = 1;
    private static final String NO_MORE_PLAYERS_CONNECTED = "No more players connected";
    private static final Logger logger = LoggerFactory.getLogger(GameThread.class);

    protected final GameThreadParams params;
    private final AtomicBoolean interruptGame = new AtomicBoolean(false);
    private final AtomicBoolean roundInProgress = new AtomicBoolean(false);
    private final AtomicBoolean gameInProgress = new AtomicBoolean(false);

    private List<Card> deckOfCards;
    private int deckCardPointer;
    protected PokerTable pokerTable;
    protected Round currentRound;
    protected List<PlayerSession> playerSessions;

    @Autowired
    protected GameThreadManager threadManager;
    @Autowired
    protected ServerMessageFactory messageFactory;
    @Autowired
    protected MessageDispatcher dispatcher;
    @Autowired
    protected TableRepository tableRepository;
    @Autowired
    protected RoundRepository roundRepository;
    @Autowired
    protected RoundService roundService;
    @Autowired
    protected PlayerSessionRepository playerSessionRepository;
    @Autowired
    protected HandService handService;
    @Autowired
    protected HandRepository handRepository;
    @Autowired
    protected CardService cardService;
    @Autowired
    protected CardRepository cardRepository;
    @Autowired
    protected HandEvaluator handEvaluator;

    @Override
    public void run() {
        initializeThread();
        initializeTable();

        waitForPlayersToJoin(MINIMUM_PLAYERS_CONNECTED);

        while (isPlayersJoined(MINIMUM_PLAYERS_CONNECTED)) {
            if (isGameInterrupted()) return;
            waitForPlayersToJoinNotZero();
            if (isGameInterrupted()) return;
            while (isPlayersJoined()) {
                if (isGameInterrupted()) return;
                createNewRound();
                if (isGameInterrupted()) return;
                initRound();
                if (isGameInterrupted()) return;
                runRound();
                if (isGameInterrupted()) return;
                finishRound();
                if (isGameInterrupted()) return;
            }
        }
        finishGame();
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
        Optional<PokerTable> tableOpt = tableRepository.findById(params.getTableId());
        if (tableOpt.isEmpty()) {
            fail("No table found, cannot start game");
        } else {
            pokerTable = tableOpt.get();
        }
    }

    private void waitForPlayersToJoinNotZero() {
        GameType gameType = pokerTable.getGameType();
        do {
            if (isGameInterrupted()) return;
            playerSessions = playerSessionRepository.findConnectedByTableIdPessimistic(params.getTableId());
            if (CollectionUtils.isEmpty(playerSessions)) {
                fail(NO_MORE_PLAYERS_CONNECTED);
                return;
            }
            if (playerSessions.size() >= gameType.getMinPlayerCount()) {
                return;
            }
            sendLogMessage("Waiting for players to join...");
            sleepInMs(DB_POLL_WAIT_MS);
        } while (playerSessions.size() < gameType.getMinPlayerCount());
    }

    private void waitForPlayersToJoin(int minPlayerCount) {
        do {
            if (isGameInterrupted()) return;
            playerSessions = playerSessionRepository.findConnectedByTableIdPessimistic(params.getTableId());
            if (playerSessions.size() >= minPlayerCount) {
                return;
            }
            sendLogMessage("Waiting for players to join...");
            sleepInMs(DB_POLL_WAIT_MS);
        } while (playerSessions.size() < minPlayerCount);
    }

    private void createNewRound() {
        Optional<Round> roundOpt = roundRepository
                .findCurrentByTableId(params.getTableId());
        if (roundOpt.isPresent()) {
            currentRound = roundOpt.get();
            if (currentRound.getRoundState() != RoundState.WAITING_FOR_PLAYERS) {
                fail("Cannot start an existing new round not in the WAITING_FOR_PLAYERS state");
            }
        } else {
            Optional<PokerTable> tableOpt = tableRepository.findById(params.getTableId());
            if (tableOpt.isEmpty()) {
                fail("Cannot start as table doesn't exist");
            } else {
                currentRound = roundService.create(pokerTable);
            }
        }
        sendLogMessage("New Round...");
    }

    private boolean isPlayersJoined() {
        GameType gameType = pokerTable.getGameType();
        int minPlayerCount = gameType.getMinPlayerCount();
        return isPlayersJoined(minPlayerCount);
    }

    private boolean isPlayersJoined(int count) {
        playerSessions = playerSessionRepository
                .findConnectedByTableIdPessimistic(params.getTableId());
        return playerSessions.size() >= count;
    }

    protected void checkAtLeastOnePlayerConnected() {
        if (CollectionUtils.isEmpty(playerSessions)) {
            fail(NO_MORE_PLAYERS_CONNECTED);
        }
    }

    private void initRound() {
        roundInProgress.set(true);
        shuffleCards();
        onRoundInit();
    }

    private void runRound() {
        RoundState roundState = RoundState.INIT_DEAL;
        saveRoundState(roundState);
        while (roundState != RoundState.FINISH) {
            if (isGameInterrupted()) return;
            onRunRound(roundState);
            roundState = getNextRoundState(roundState);
            saveRoundState(roundState);
        }
    }

    // ***************************************************************
    // Abstract Methods
    // ***************************************************************

    abstract protected void onRoundInit();

    abstract protected void onRunRound(RoundState roundState);

    abstract protected RoundState getNextRoundState(RoundState roundState);

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

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

    private void finishRound() {
        if (roundInProgress.get()) {
            saveRoundState(RoundState.FINISH);
            dispatcher.send(params.getTableId(), messageFactory.roundFinished());
        }
        roundInProgress.set(false);
    }

    private void finishGame() {
        if (gameInProgress.get()) {
            dispatcher.send(params.getTableId(), messageFactory.gameFinished());
            threadManager.delete(params.getTableId());
        }
        gameInProgress.set(false);
    }

    protected void sendLogMessage(String message) {
        dispatcher.send(params.getTableId(), messageFactory.logMessage(message));
    }

    protected void sleepInMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            fail("Failed to sleep for " + ms);
        }
    }

    public void onPlayerDisconnected(String username) {
        // potentially fold username


        List<PlayerSession> playerSessions =
                playerSessionRepository.findConnectedByTableIdPessimistic(params.getTableId());
        if (CollectionUtils.isEmpty(playerSessions)) {
            fail(NO_MORE_PLAYERS_CONNECTED);
        }
    }

    protected void fail(String message) {
        logger.error(message);
        sendLogMessage(message);
        interruptGame.set(true);
    }

    protected boolean isGameInterrupted() {
        if (interruptGame.get() || interrupted()) {
            finishRound();
            finishGame();
            return true;
        }
        return false;
    }

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
}
