package com.twb.pokergame.service.game.runnable;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.repository.RoundRepository;
import com.twb.pokergame.repository.TableRepository;
import com.twb.pokergame.service.RoundService;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
public abstract class GameRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GameRunnable.class);

    protected final UUID tableId;
    protected final int minNumberOfPlayers;

    protected Round currentRound;

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

    @Override
    public void run() {

        sendLogMessage("Initializing the round...");
        Optional<Round> roundOpt = roundRepository.findCurrentByTableId(tableId);
        if (roundOpt.isPresent()) {
            currentRound = roundOpt.get();
            if (currentRound.getRoundState() != RoundState.INIT) {
                logger.warn("Cannot start an existing new round not in the INIT state");
                return;
            }
        } else {
            Optional<PokerTable> tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isEmpty()) {
                logger.warn("Cannot start as table doesnt exist");
                return;
            }
            PokerTable table = tableOpt.get();
            currentRound = roundService.createSingle(table);
        }
        sendLogMessage("Round Initialized.");


        sendLogMessage("Waiting for players to join...");


        //wait for players to join the round...


//        this.gameState = GameState.WAITING_FOR_PLAYERS;
//        sendLogMessage("Waiting for players to join...");
//        List<Round> pokerTableUsers;
//        do {
//            pokerTableUsers = roundRepository.findByPokerTableId(tableId);
//            if (pokerTableUsers.size() < minNumberOfPlayers) {
//                sleep(300);
//            }
//        } while (pokerTableUsers.size() < minNumberOfPlayers);
//        sendLogMessage("Game Starting...");
//        this.gameState = GameState.GAME_STARTING;

        onRun();

//        this.gameState = GameState.GAME_ENDED;
        sendLogMessage("Game Ended.");
    }

    abstract protected void onRun();


    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    protected void sendLogMessage(String log) {
        ServerMessageDTO message = messageFactory.logMessage(log);
        dispatcher.send(tableId, message);
    }

    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to sleep for " + milliseconds, e);
        }
    }
}
