package com.twb.pokergame.service.game.runnable;

import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.repository.PlayerSessionRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
public abstract class GameRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GameRunnable.class);

    protected final UUID tableId;

    protected PokerTable pokerTable;
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

    @Autowired
    protected PlayerSessionRepository playerSessionRepository;

    @Override
    public void run() {
        // -----------------------------------------------------------------------

        sendLogMessage("Initializing the round...");
        Optional<Round> roundOpt = roundRepository.findCurrentByTableId(tableId);
        if (roundOpt.isPresent()) {
            currentRound = roundOpt.get();
            pokerTable = currentRound.getPokerTable();
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
            pokerTable = tableOpt.get();
            currentRound = roundService.createSingle(pokerTable);
        }
        sendLogMessage("Round Initialized.");

        // -----------------------------------------------------------------------

        currentRound.setRoundState(RoundState.WAITING_FOR_PLAYERS);
        roundRepository.saveAndFlush(currentRound);
        sendLogMessage("Waiting for players to join...");

        GameType gameType = pokerTable.getGameType();
        int minPlayerCount = gameType.getMinPlayerCount();
        List<PlayerSession> sessionsConnected;
        do {
            sessionsConnected = playerSessionRepository
                    .findByTableId(tableId);
            sleep(300);
        } while (sessionsConnected.size() < minPlayerCount);

        // -----------------------------------------------------------------------

        sendLogMessage("Game Starting...");
        onRun();

        // -----------------------------------------------------------------------

        currentRound.setRoundState(RoundState.COMPLETED);
        roundRepository.saveAndFlush(currentRound);
        sendLogMessage("Game Completed");

        // -----------------------------------------------------------------------
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
