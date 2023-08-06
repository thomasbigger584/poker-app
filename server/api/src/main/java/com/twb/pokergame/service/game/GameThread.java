package com.twb.pokergame.service.game;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.repository.*;
import com.twb.pokergame.service.CardService;
import com.twb.pokergame.service.HandService;
import com.twb.pokergame.service.RoundService;
import com.twb.pokergame.service.eval.HandEvaluator;
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
import java.util.UUID;
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

    protected final UUID tableId;
    private final AtomicBoolean interruptGame = new AtomicBoolean(false);

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

        while (isPlayersJoined(MINIMUM_PLAYERS_CONNECTED)) {
            if (isGameInterrupted()) return;
            waitForPlayersToJoin();
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
        setName(tableId.toString());
        setPriority(Thread.MAX_PRIORITY);
        interruptGame.set(false);
    }

    private void initializeTable() {
        Optional<PokerTable> tableOpt = tableRepository.findById(tableId);
        if (tableOpt.isEmpty()) {
            fail("No table found, cannot start game");
        } else {
            pokerTable = tableOpt.get();
        }
    }

    private void waitForPlayersToJoin() {
        sendLogMessage("Waiting for players to join...");
        GameType gameType = pokerTable.getGameType();
        do {
            if (isGameInterrupted()) return;
            playerSessions = playerSessionRepository.findConnectedByTableId(tableId);
            checkAtLeastOnePlayerConnected();
            sleepInMs(DB_POLL_WAIT_MS);
        } while (playerSessions.size() < gameType.getMinPlayerCount());
    }

    private void createNewRound() {
        Optional<Round> roundOpt = roundRepository
                .findCurrentByTableId(tableId);
        if (roundOpt.isPresent()) {
            currentRound = roundOpt.get();
            if (currentRound.getRoundState() != RoundState.WAITING_FOR_PLAYERS) {
                fail("Cannot start an existing new round not in the WAITING_FOR_PLAYERS state");
            }
        } else {
            Optional<PokerTable> tableOpt = tableRepository.findById(tableId);
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
        if (isGameInterrupted()) return false;
        playerSessions = playerSessionRepository
                .findConnectedByTableId(tableId);
        return playerSessions.size() >= count;
    }

    protected void checkAtLeastOnePlayerConnected() {
        if (CollectionUtils.isEmpty(playerSessions)) {
            fail(NO_MORE_PLAYERS_CONNECTED);
        }
    }

    private void initRound() {
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

        //todo: test if this is getting called, is round already finished ?
        if (currentRound.getRoundState() != RoundState.FINISH) {
            saveRoundState(RoundState.FINISH);
            dispatcher.send(tableId, messageFactory.roundFinished());
        }
    }

    private void finishGame() {
        sendLogMessage("Game Finished");
        threadManager.delete(tableId);
    }

    protected void sendLogMessage(String message) {
        dispatcher.send(tableId, messageFactory.logMessage(message));
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
                playerSessionRepository.findConnectedByTableId(tableId);
        if (CollectionUtils.isEmpty(playerSessions)) {
            fail(NO_MORE_PLAYERS_CONNECTED);
        }
    }

    protected void fail(String message) {
        logger.error(message);
        sendLogMessage(message);
        interruptGame.compareAndSet(false, true);
    }

    protected boolean isGameInterrupted() {
        if (interruptGame.get() || interrupted()) {
            finishRound();
            finishGame();
            return true;
        }
        return false;
    }
}
