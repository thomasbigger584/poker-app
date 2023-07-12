package com.twb.pokergame.service.game;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PokerGameThreadHandler {
    private final ApplicationContext context;
    private final AsyncTaskExecutor taskExecutor;

    private static final Map<String, PokerGameRunnable> POKER_GAME_RUNNABLE_MAP = new HashMap<>();

    public void onPlayerConnected(String pokerTableId, String username) {

        PokerGameRunnable gameRunnable = context.getBean(PokerGameRunnable.class, pokerTableId);

        taskExecutor.execute(gameRunnable);
    }

}
