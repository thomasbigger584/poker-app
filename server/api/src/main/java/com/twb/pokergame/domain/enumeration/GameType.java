package com.twb.pokergame.domain.enumeration;

import com.twb.pokergame.service.game.GameThread;
import com.twb.pokergame.service.game.impl.BlackjackGameThread;
import com.twb.pokergame.service.game.impl.TexasHoldemGameThread;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

public enum GameType {
    TEXAS_HOLDEM,
    BLACKJACK;

    // TEXAS_HOLDEM
    public static final int TEXAS_HOLDEM_MAX_PLAYER_COUNT = 6;
    public static final int TEXAS_HOLDEM_MIN_PLAYER_COUNT = 2;

    // BLACKJACK
    public static final int BLACKJACK_MAX_PLAYER_COUNT = 1;
    public static final int BLACKJACK_MIN_PLAYER_COUNT = 1;

    public int getMaxPlayerCount() {
        return switch (this) {
            case TEXAS_HOLDEM -> TEXAS_HOLDEM_MAX_PLAYER_COUNT;
            case BLACKJACK -> BLACKJACK_MAX_PLAYER_COUNT;
        };
    }

    public int getMinPlayerCount() {
        return switch (this) {
            case TEXAS_HOLDEM -> TEXAS_HOLDEM_MIN_PLAYER_COUNT;
            case BLACKJACK -> BLACKJACK_MIN_PLAYER_COUNT;
        };
    }

    public GameThread getGameThread(ApplicationContext context, UUID tableId) {
        return switch (this) {
            case TEXAS_HOLDEM -> context.getBean(TexasHoldemGameThread.class, tableId);
            case BLACKJACK -> context.getBean(BlackjackGameThread.class, tableId);
        };
    }
}
