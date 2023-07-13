package com.twb.pokergame.service.game.runnable;

import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessage;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


@RequiredArgsConstructor
public abstract class GameRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GameRunnable.class);

    protected final UUID pokerTableId;

    @Autowired
    protected ServerMessageFactory messageFactory;

    @Autowired
    protected MessageDispatcher dispatcher;

    protected void sendLogMessage(String log) {
        ServerMessage message = messageFactory.logMessage(log);
        dispatcher.send(pokerTableId, message);
    }

    public void onPlayerConnected(String username) {
        logger.info("Player Connected " + username);

        ServerMessage serverMessage = messageFactory.playerConnected(username);
        dispatcher.send(pokerTableId, serverMessage);
    }

    public void onPlayerDisconnected(String username) {
        logger.info("Player Disconnected " + username);

        ServerMessage serverMessage = messageFactory.playerDisconnected(username);
        dispatcher.send(pokerTableId, serverMessage);
    }
}
