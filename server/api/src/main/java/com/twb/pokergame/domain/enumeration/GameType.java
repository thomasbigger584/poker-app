package com.twb.pokergame.domain.enumeration;

public enum GameType {
    TEXAS_HOLDEM,
    BLACKJACK;

    public static final int TEXAS_HOLDEM_MAX_PLAYER_COUNT = 6;
    public static final int BLACKJACK_MAX_PLAYER_COUNT = 1;
    public static final int TEXAS_HOLDEM_MIN_PLAYER_COUNT = 2;
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
}
