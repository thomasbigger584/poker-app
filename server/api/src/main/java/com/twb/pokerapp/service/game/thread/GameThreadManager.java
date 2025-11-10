package com.twb.pokerapp.service.game.thread;

import com.antkorwin.xsync.XSync;
import com.twb.pokerapp.domain.PokerTable;
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

/**
 * Manages game threads for poker tables.
 */
@Component
@RequiredArgsConstructor
public class GameThreadManager {
    private static final Logger logger = LoggerFactory.getLogger(GameThreadManager.class);
    private static final Map<UUID, GameThread> POKER_GAME_RUNNABLE_MAP = new ConcurrentHashMap<>();
    private static final int GAME_START_TIMEOUT_IN_SECS = 10;
    private final XSync<UUID> mutex;
    private final ApplicationContext context;

    /**
     * Creates a game thread for the given poker table if it does not already exist.
     *
     * @param pokerTable the poker table
     * @return the created or existing game thread
     */
    public GameThread createIfNotExist(PokerTable pokerTable) {
        return mutex.evaluate(pokerTable.getId(), () -> {
            var threadOpt = getIfExists(pokerTable);
            if (threadOpt.isPresent()) {
                return threadOpt.get();
            }
            var params = getGameThreadParams(pokerTable);
            var thread = create(params);
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

    /**
     * Deletes the game thread for the given table ID.
     *
     * @param tableId the table ID
     * @return true if the game thread was deleted, false otherwise
     */
    public boolean delete(UUID tableId) {
        return mutex.evaluate(tableId, () -> {
            var threadOpt = getIfExists(tableId);
            if (threadOpt.isPresent()) {
                POKER_GAME_RUNNABLE_MAP.remove(tableId);
                return true;
            }
            return false;
        });
    }

    // -------------------------------------------------------------------------------------

    /**
     * Retrieves game thread parameters for the given poker table.
     *
     * @param pokerTable the poker table
     * @return the game thread parameters
     */
    private GameThreadParams getGameThreadParams(PokerTable pokerTable) {
        return GameThreadParams.builder()
                .tableId(pokerTable.getId())
                .gameType(pokerTable.getGameType())
                .startLatch(new CountDownLatch(1))
                .build();
    }

    /**
     * Creates a new game thread with the given parameters.
     *
     * @param params the game thread parameters
     * @return the created game thread
     */
    private GameThread create(GameThreadParams params) {
        return params.getGameType()
                .getGameThread(context, params);
    }

    /**
     * Retrieves the game thread for the given poker table if it exists.
     *
     * @param pokerTable the poker table
     * @return an Optional containing the game thread if it exists, otherwise empty
     */
    public Optional<GameThread> getIfExists(PokerTable pokerTable) {
        return getIfExists(pokerTable.getId());
    }

    /**
     * Retrieves the game thread for the given table ID if it exists.
     *
     * @param tableId the table ID
     * @return an Optional containing the game thread if it exists, otherwise empty
     */
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
