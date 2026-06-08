package com.twb.pokerapp.web.websocket.session;

import com.twb.pokerapp.service.UserWebsocketService;
import com.twb.pokerapp.service.game.TableGameService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Defers player disconnection after a websocket drops, giving the client a grace window to
 * reconnect (e.g. after a transient network blip or the app being backgrounded) before the player
 * is actually removed from the table.
 *
 * <p>When the grace period elapses the disconnect only proceeds if the user is genuinely still
 * gone — a fresh session opened by a reconnect (even one that races with the old session's close)
 * is detected via {@link UserWebsocketService}, so a reconnected player is never disconnected.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DisconnectGraceService {

    @Value("${app.disconnect-grace-period-seconds:20}")
    private long gracePeriodSeconds;

    private final TableGameService tableGameService;
    private final UserWebsocketService userWebsocketService;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                var thread = new Thread(runnable, "disconnect-grace");
                thread.setDaemon(true);
                return thread;
            });

    private final ConcurrentHashMap<String, ScheduledFuture<?>> pending = new ConcurrentHashMap<>();

    /**
     * Schedule the eventual disconnection of a user whose session just dropped. Any previously
     * scheduled disconnect for the same user/table is replaced.
     */
    public void scheduleDisconnect(UUID tableId, String username) {
        var key = key(tableId, username);
        cancelPending(tableId, username);
        var future = scheduler.schedule(
                () -> runDisconnect(key, tableId, username), gracePeriodSeconds, TimeUnit.SECONDS);
        pending.put(key, future);
        log.debug("Scheduled disconnect for {} on table {} in {}s", username, tableId, gracePeriodSeconds);
    }

    /**
     * Milliseconds left before a dropped user is actually disconnected from the table, or empty if
     * there is no pending disconnect (the user is still genuinely connected, or already gone). Lets
     * the client count the grace window down and flip "Reconnect" back to "Connect" on expiry.
     */
    public Optional<Long> getRemainingMillis(UUID tableId, String username) {
        var future = pending.get(key(tableId, username));
        if (future == null || future.isDone()) {
            return Optional.empty();
        }
        var remaining = future.getDelay(TimeUnit.MILLISECONDS);
        return remaining > 0 ? Optional.of(remaining) : Optional.empty();
    }

    /**
     * Cancel a pending disconnect because the user (re)subscribed to the table.
     */
    public void cancelPending(UUID tableId, String username) {
        var future = pending.remove(key(tableId, username));
        if (future != null) {
            future.cancel(false);
            log.debug("Cancelled pending disconnect for {} on table {} (reconnected)", username, tableId);
        }
    }

    private void runDisconnect(String key, UUID tableId, String username) {
        pending.remove(key);
        try {
            if (userWebsocketService.isUserConnectedToTable(tableId, username)) {
                log.info("User {} reconnected to table {} within grace period; not disconnecting", username, tableId);
                return;
            }
            log.info("Grace period elapsed for {} on table {}; disconnecting", username, tableId);
            tableGameService.onUserDisconnected(tableId, username);
        } catch (Exception e) {
            log.error("Error during graceful disconnect of {} on table {}", username, tableId, e);
        }
    }

    private String key(UUID tableId, String username) {
        return tableId + ":" + username;
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
