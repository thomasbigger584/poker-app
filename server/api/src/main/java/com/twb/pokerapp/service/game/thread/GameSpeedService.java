package com.twb.pokerapp.service.game.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameSpeedService {
    private static final long MIN_NETWORK_FLOOR = 250;

    @Value("${app.speed-multiplier:1}")
    private double multiplier;

    public void sleep(long delay) {
        var calculatedDelay = (long) (delay / multiplier);
        var finalDelay = Math.max(calculatedDelay, MIN_NETWORK_FLOOR);
        try {
            Thread.sleep(finalDelay);
        } catch (InterruptedException e) {
            log.error("Failed to sleep", e);
            Thread.currentThread().interrupt();
        }
    }
}
