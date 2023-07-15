package com.twb.pokergame.service.game.runnable;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.service.game.runnable.impl.BlackjackGameRunnable;
import com.twb.pokergame.service.game.runnable.impl.TexasHoldemGameRunnable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class GameRunnableFactory {
    private static final Logger logger = LoggerFactory.getLogger(GameRunnableFactory.class);
    private static final Map<UUID, GameRunnable> POKER_GAME_RUNNABLE_MAP = new ConcurrentHashMap<>();

    private final ApplicationContext context;
    private final AsyncTaskExecutor taskExecutor;

    public GameRunnable createIfNotExist(PokerTable pokerTable) {
        Optional<GameRunnable> runnableOpt = getIfExists(pokerTable);
        if (runnableOpt.isPresent()) {
            return runnableOpt.get();
        }
        GameRunnable runnable = create(pokerTable);
        taskExecutor.execute(runnable);
        POKER_GAME_RUNNABLE_MAP.put(pokerTable.getId(), runnable);
        return runnable;
    }

    private GameRunnable create(PokerTable pokerTable) {
        UUID tableId = pokerTable.getId();
        return switch (pokerTable.getGameType()) {
            case TEXAS_HOLDEM -> context.getBean(TexasHoldemGameRunnable.class, tableId);
            case BLACKJACK -> context.getBean(BlackjackGameRunnable.class, tableId);
        };
    }

    public Optional<GameRunnable> getIfExists(PokerTable pokerTable) {
        return getIfExists(pokerTable.getId());
    }

    public Optional<GameRunnable> getIfExists(String tableId) {
        return getIfExists(UUID.fromString(tableId));
    }

    public Optional<GameRunnable> getIfExists(UUID tableId) {
        if (!POKER_GAME_RUNNABLE_MAP.containsKey(tableId)) {
            logger.warn("Poker Table {} doesn't have a game thread running.", tableId);
            return Optional.empty();
        }
        return Optional.of(POKER_GAME_RUNNABLE_MAP.get(tableId));
    }

    public void delete(UUID tableId) {
        Optional<GameRunnable> runnableOpt = getIfExists(tableId);
        if (runnableOpt.isPresent()) {
            POKER_GAME_RUNNABLE_MAP.remove(tableId);
        }
    }
}
