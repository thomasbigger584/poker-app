package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PokerTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class GameSpeedService {
    private static final long MIN_NETWORK_FLOOR = 250;
    private static final long MIN_PLAYER_TURN_WAIT = 5000;

    public void sleep(PokerTable table, long delay) {
        var speedMultiplier = table.getSpeedMultiplier();
        var finalDelay = getFinalDelay(speedMultiplier, delay, MIN_NETWORK_FLOOR);
        sleep(finalDelay);
    }

    public Long getPlayerTurnWait(BettingRound bettingRound, long playerTurnWaitMs) {
        var speedMultiplier = bettingRound.getRound().getPokerTable().getSpeedMultiplier();
        return getFinalDelay(speedMultiplier, playerTurnWaitMs, MIN_PLAYER_TURN_WAIT);
    }

    public void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.error("Failed to sleep", e);
            Thread.currentThread().interrupt();
        }
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private long getFinalDelay(Double speedMultiplier, long delay, long floor) {
        var multiplier = Optional.ofNullable(speedMultiplier).orElse(1d);
        var calculatedDelay = (long) (delay / multiplier);
        return Math.max(calculatedDelay, floor);
    }
}
