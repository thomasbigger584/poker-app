package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.PokerTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class GameSpeedService {
    private static final long MIN_NETWORK_FLOOR = 250;

    public void sleep(PokerTable table, long delay) {
        var multiplier = Optional.ofNullable(table.getSpeedMultiplier()).orElse(1d);
        var calculatedDelay = (long) (delay / multiplier);
        var finalDelay = Math.max(calculatedDelay, MIN_NETWORK_FLOOR);
        sleep(finalDelay);
    }

    public void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.error("Failed to sleep", e);
            Thread.currentThread().interrupt();
        }
    }
}
