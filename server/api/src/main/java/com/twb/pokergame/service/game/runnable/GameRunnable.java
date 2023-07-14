package com.twb.pokergame.service.game.runnable;

import com.twb.pokergame.domain.PokerTableUser;
import com.twb.pokergame.repository.PokerTableUserRepository;
import com.twb.pokergame.service.game.runnable.impl.enumeration.GameState;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
public abstract class GameRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GameRunnable.class);

    protected final UUID pokerTableId;

    protected GameState gameState = GameState.GAME_INIT;

    @Autowired
    protected ServerMessageFactory messageFactory;

    @Autowired
    protected MessageDispatcher dispatcher;

    @Autowired
    protected PokerTableUserRepository pokerTableUserRepository;

    @Override
    public void run() {
        this.gameState = GameState.WAITING_FOR_PLAYERS;
        sendLogMessage("Waiting for players to join...");
        List<PokerTableUser> userSessions;
        do {
            userSessions = pokerTableUserRepository.findByPokerTableId(pokerTableId);
            if (userSessions.isEmpty()) {
                sendLogMessage("No more players so stopping");
                return;
            }
            sleep(300);
        } while (userSessions.size() < getMinNumberOfPlayers());

        sendLogMessage("Game Starting...");
        this.gameState = GameState.GAME_STARTING;

        onRun();

        this.gameState = GameState.GAME_ENDED;
        sendLogMessage("Game Starting...");
    }

    abstract protected int getMinNumberOfPlayers();

    abstract protected void onRun();


    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    protected void sendLogMessage(String log) {
        ServerMessageDTO message = messageFactory.logMessage(log);
        dispatcher.send(pokerTableId, message);
    }

    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to sleep for " + milliseconds, e);
        }
    }
}
