package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PokerTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class GameSpeedService {
    private static final long MIN_NETWORK_FLOOR = 250;
    private static final long MIN_PLAYER_TURN_WAIT = 5000;
    private static final long MIN_BOT_THINK_WAIT = 2000;
    private static final long MAX_BOT_THINK_WAIT = 4000;
    // Largest share of the player-turn limit a bot's total turn (processing + thinking) may consume,
    // leaving headroom so it always acts before the turn times out.
    private static final double MAX_BOT_THINK_TURN_FRACTION = 0.8;

    public void sleep(PokerTable table, long delay) {
        var speedMultiplier = table.getSpeedMultiplier();
        var finalDelay = getFinalDelay(speedMultiplier, delay, MIN_NETWORK_FLOOR);
        sleep(finalDelay);
    }

    public Long getPlayerTurnWait(BettingRound bettingRound, long playerTurnWaitMs) {
        var speedMultiplier = bettingRound.getRound().getPokerTable().getSpeedMultiplier();
        return getFinalDelay(speedMultiplier, playerTurnWaitMs, MIN_PLAYER_TURN_WAIT);
    }

    /**
     * Sleeps for whatever remains of a short, randomised "thinking" budget after the caller has
     * already spent time computing the bot's action. The base budget of {@value #MIN_BOT_THINK_WAIT}ms
     * to {@value #MAX_BOT_THINK_WAIT}ms is scaled down by the table speed multiplier so bots respond
     * proportionally faster on faster tables, and is then capped at {@value #MAX_BOT_THINK_TURN_FRACTION}
     * of the player-turn limit ({@link #getPlayerTurnWait}) so the bot always acts within its turn and
     * never hits the timeout. If the processing already took longer than the budget, it does not sleep
     * at all.
     *
     * @param bettingRound     the current betting round, used to read the table speed multiplier
     * @param playerTurnWaitMs the base (un-scaled) player turn wait, in milliseconds
     * @param turnStartMillis  the {@link System#currentTimeMillis()} captured when the bot's turn began
     */
    public void sleepBotThinkingTime(BettingRound bettingRound, long playerTurnWaitMs, long turnStartMillis) {
        var speedMultiplier = bettingRound.getRound().getPokerTable().getSpeedMultiplier();
        var multiplier = Optional.ofNullable(speedMultiplier).orElse(1d);

        // Faster tables (higher multiplier) shrink the think budget so bots keep pace with the table.
        var minThink = (long) (MIN_BOT_THINK_WAIT / multiplier);
        var maxThink = (long) (MAX_BOT_THINK_WAIT / multiplier);

        // Never let the think budget approach the turn limit, so the bot always acts in time.
        var thinkCeiling = (long) (getPlayerTurnWait(bettingRound, playerTurnWaitMs) * MAX_BOT_THINK_TURN_FRACTION);
        maxThink = Math.min(maxThink, thinkCeiling);
        minThink = Math.min(minThink, maxThink);

        var thinkBudgetMs = ThreadLocalRandom.current().nextLong(minThink, maxThink + 1);
        var remainingMs = thinkBudgetMs - (System.currentTimeMillis() - turnStartMillis);
        if (remainingMs > 0) {
            sleep(remainingMs);
        }
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
