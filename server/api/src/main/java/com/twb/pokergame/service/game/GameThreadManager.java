package com.twb.pokergame.service.game;

import com.antkorwin.xsync.XSync;
import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.domain.enumeration.GameType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class GameThreadManager {
    private static final Logger logger = LoggerFactory.getLogger(GameThreadManager.class);
    private static final Map<UUID, GameThread> POKER_GAME_RUNNABLE_MAP = new ConcurrentHashMap<>();
    private static final int GAME_START_TIMEOUT_IN_SECS = 10;
    private final XSync<UUID> mutex;
    private final ApplicationContext context;

    public GameThread createIfNotExist(PokerTable pokerTable) {
        return mutex.evaluate(pokerTable.getId(), () -> {
            Optional<GameThread> threadOpt = getIfExists(pokerTable);
            if (threadOpt.isPresent()) {
                return threadOpt.get();
            }
            GameThreadParams params = getGameThreadParams(pokerTable);
            GameThread thread = create(params);
            long startTime = System.currentTimeMillis();
            thread.start();
            try {
                if (!params.getStartLatch().await(GAME_START_TIMEOUT_IN_SECS, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Failed to wait for game to start");
                }
                POKER_GAME_RUNNABLE_MAP.put(pokerTable.getId(), thread);
                return thread;
            } catch (InterruptedException e) {
                throw new RuntimeException("Exception thrown while waiting for game to start", e);
            }
        });
    }

    public boolean delete(UUID tableId) {
        return mutex.evaluate(tableId, () -> {
            Optional<GameThread> threadOpt = getIfExists(tableId);
            if (threadOpt.isPresent()) {
                POKER_GAME_RUNNABLE_MAP.remove(tableId);
                return true;
            }
            return false;
        });
    }

    // -------------------------------------------------------------------------------------

    private GameThreadParams getGameThreadParams(PokerTable pokerTable) {
        return GameThreadParams.builder()
                .tableId(pokerTable.getId())
                .gameType(pokerTable.getGameType())
                .startLatch(new CountDownLatch(1))
                .build();
    }

    private GameThread create(GameThreadParams params) {
        GameType gameType = params.getGameType();
        return gameType.getGameThread(context, params);
    }

    public Optional<GameThread> getIfExists(PokerTable pokerTable) {
        return getIfExists(pokerTable.getId());
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
