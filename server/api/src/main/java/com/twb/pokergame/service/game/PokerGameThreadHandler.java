package com.twb.pokergame.service.game;

import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.server.ServerMessage;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PokerGameThreadHandler {
    private static final Logger logger = LoggerFactory.getLogger(PokerGameThreadHandler.class);
    private static final Map<String, PokerGameRunnable> POKER_GAME_RUNNABLE_MAP = new HashMap<>();
    private final ApplicationContext context;
    private final AsyncTaskExecutor taskExecutor;

    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;

    public void onPlayerConnected(String pokerTableId, String username) {
        PokerGameRunnable gameRunnable;

        if (POKER_GAME_RUNNABLE_MAP.containsKey(pokerTableId)) {
            gameRunnable = POKER_GAME_RUNNABLE_MAP.get(pokerTableId);
        } else {
            gameRunnable = context.getBean(PokerGameRunnable.class, pokerTableId);
            taskExecutor.execute(gameRunnable);
            POKER_GAME_RUNNABLE_MAP.put(pokerTableId, gameRunnable);
        }

        gameRunnable.onPlayerConnected(username);

        ServerMessage serverMessage = messageFactory.playerConnected(username);
        dispatcher.send(pokerTableId, serverMessage);
    }

    public void onPlayerDisconnected(String pokerTableId, String username) {
        if (!POKER_GAME_RUNNABLE_MAP.containsKey(pokerTableId)) {
            logger.warn("Poker Table {} doesn't have a game thread running.", pokerTableId);
            return;
        }

        PokerGameRunnable gameRunnable = POKER_GAME_RUNNABLE_MAP.get(pokerTableId);
        gameRunnable.onPlayerDisconnected(username);

    }

}
