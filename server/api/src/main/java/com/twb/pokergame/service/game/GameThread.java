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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
public abstract class GameThread extends Thread {
    protected static final SecureRandom RANDOM = new SecureRandom();
    protected static final int WAIT_MS = 1000;
    private static final Logger logger = LoggerFactory.getLogger(GameThread.class);

    protected final UUID tableId;
    protected PokerTable pokerTable;
    protected Round currentRound;
    protected List<PlayerSession> playerSessions;
    protected List<Card> deckOfCards;
    protected int deckCardPointer;

    // --------------------------------------------
    @Autowired
    protected GameThreadFactory threadFactory;

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
        initializeTable();
        waitForPlayersToJoin();
        while (isPlayersJoined()) {
            createNewRound();
            initRound();
            runRound();
            finishRound();
        }
        finish();
    }

    private void initializeTable() {
        Optional<PokerTable> tableOpt = tableRepository.findById(tableId);
        if (tableOpt.isPresent()) {
            pokerTable = tableOpt.get();
        } else {
            fail("No table found, cannot start game");
        }
    }

    private void waitForPlayersToJoin() {
        sendLogMessage("Waiting for players to join...");
        GameType gameType = pokerTable.getGameType();
        do {
            playerSessions = playerSessionRepository.findConnectedByTableId(tableId);
            sleepInMs(WAIT_MS);
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
                currentRound = roundService.createSingle(pokerTable);
            }
        }
        sendLogMessage("New Round...");
    }


    private boolean isPlayersJoined() {
        GameType gameType = pokerTable.getGameType();
        playerSessions = playerSessionRepository.findConnectedByTableId(tableId);
        return playerSessions.size() >= gameType.getMinPlayerCount();
    }

    private void initRound() {
        shuffleCards();
        onRoundInit();
    }

    private void runRound() {
        RoundState roundState = RoundState.INIT_DEAL;
        saveRoundState(roundState);
        while (roundState != RoundState.FINISH) {
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
        saveRoundState(RoundState.FINISH);
        dispatcher.send(tableId, messageFactory.roundFinished());
    }

    private void finish() {
        threadFactory.delete(tableId);
        sendLogMessage("Game Finished");
    }

    protected void sendLogMessage(String message) {
        dispatcher.send(tableId, messageFactory.logMessage(message));
    }

    protected void sleepInMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to sleep for " + ms, e);
        }
    }

    protected void fail(String message) {
        logger.error(message);
        sendLogMessage(message);
        finishRound();
        threadFactory.delete(tableId);
        interrupt();
    }
}
