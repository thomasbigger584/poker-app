package com.twb.pokergame.service.game;

import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.old.CardDTO;
import com.twb.pokergame.old.DeckOfCardsFactory;
import com.twb.pokergame.repository.*;
import com.twb.pokergame.service.CardService;
import com.twb.pokergame.service.HandService;
import com.twb.pokergame.service.RoundService;
import com.twb.pokergame.service.eval.HandEvaluator;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
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
    protected static final int WAIT_MS = 400;
    private static final Logger logger = LoggerFactory.getLogger(GameThread.class);

    protected final UUID tableId;
    protected PokerTable pokerTable;
    protected Round currentRound;
    protected List<PlayerSession> playerSessions;
    protected List<CardDTO> cards;
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
        // -----------------------------------------------------------------------

        sendLogMessage("Waiting for players to join...");
        Optional<Round> roundOpt = roundRepository
                .findCurrentByTableId(tableId);
        if (roundOpt.isPresent()) {
            currentRound = roundOpt.get();
            pokerTable = currentRound.getPokerTable();
            if (currentRound.getRoundState() != RoundState.WAITING_FOR_PLAYERS) {
                fail("Cannot start an existing new round not in the WAITING_FOR_PLAYERS state");
                return;
            }
        } else {
            Optional<PokerTable> tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isEmpty()) {
                fail("Cannot start as table doesn't exist");
                return;
            }
            pokerTable = tableOpt.get();
            currentRound = roundService.createSingle(pokerTable);
        }

        GameType gameType = pokerTable.getGameType();
        int minPlayerCount = gameType.getMinPlayerCount();
        do {
            playerSessions = playerSessionRepository.findByTableId(tableId);
            sleepInMs(400);
        } while (playerSessions.size() < minPlayerCount);

        sendLogMessage("Round Initialized.");

        // -----------------------------------------------------------------------

        sendLogMessage("Game Starting...");

//        while (playerSessions.size() > 1) {
        onRun();
//        }

        // -----------------------------------------------------------------------

        sendLogMessage("Game Finished");

        // -----------------------------------------------------------------------
    }

    abstract protected void onRun();

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

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

    protected void saveRoundState(RoundState roundState) {
        currentRound.setRoundState(roundState);
        roundRepository.saveAndFlush(currentRound);
    }

    protected void shuffleCards() {
        cards = DeckOfCardsFactory.getCards(true);
        deckCardPointer = 0;
    }

    protected CardDTO getCard() {
        CardDTO card = cards.get(deckCardPointer);
        deckCardPointer++;
        return card;
    }

    protected void fail(String message) {
        logger.error(message);
        sendLogMessage(message);
        threadFactory.delete(tableId);
        interrupt();
    }

    public void playerConnected() {
        //todo: need to do something here?
    }

    public void playerDisconnected() {
        //todo: need to do something here?
    }
}
