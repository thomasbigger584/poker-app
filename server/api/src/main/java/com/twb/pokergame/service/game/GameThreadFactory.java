package com.twb.pokergame.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.enumeration.GameType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class GameThreadFactory {
    private static final Logger logger = LoggerFactory.getLogger(GameThreadFactory.class);
    private static final Map<UUID, GameThread> POKER_GAME_RUNNABLE_MAP = new ConcurrentHashMap<>();
    private final XSync<UUID> mutex;
    private final ApplicationContext context;

    public GameThread createIfNotExist(PokerTable pokerTable) {
        return mutex.evaluate(pokerTable.getId(), () -> {
            Optional<GameThread> threadOpt = getIfExists(pokerTable);
            if (threadOpt.isPresent()) {
                return threadOpt.get();
            }
            GameThread thread = create(pokerTable);
            thread.start();
            POKER_GAME_RUNNABLE_MAP.put(pokerTable.getId(), thread);
            return thread;
        });
    }

    public boolean delete(UUID tableId) {
        return mutex.evaluate(tableId, () -> {
            Optional<GameThread> runnableOpt = getIfExists(tableId);
            if (runnableOpt.isPresent()) {
                GameThread thread = runnableOpt.get();
                POKER_GAME_RUNNABLE_MAP.remove(tableId);
                thread.interrupt();
                return true;
            }
            return false;
        });
    }

    // -------------------------------------------------------------------------------------

    private GameThread create(PokerTable pokerTable) {
        UUID tableId = pokerTable.getId();
        GameType gameType = pokerTable.getGameType();

        return gameType.getGameThread(context, tableId);
    }

    public Optional<GameThread> getIfExists(PokerTable pokerTable) {
        return getIfExists(pokerTable.getId());
    }

    public Optional<GameThread> getIfExists(String tableId) {
        return getIfExists(UUID.fromString(tableId));
    }

    public Optional<GameThread> getIfExists(UUID tableId) {
        return mutex.evaluate(tableId, () -> {
            if (!POKER_GAME_RUNNABLE_MAP.containsKey(tableId)) {
                logger.warn("Poker Table {} doesn't have a game thread running.", tableId);
                return Optional.empty();
            }
            return Optional.of(POKER_GAME_RUNNABLE_MAP.get(tableId));
        });
    }
}
