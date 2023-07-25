package com.twb.pokergame.domain.enumeration;

public enum CardType {
    PLAYER_CARD_1,
    PLAYER_CARD_2,
    FLOP_CARD_1,
    FLOP_CARD_2,
    FLOP_CARD_3,
    TURN_CARD,
    RIVER_CARD;

    public static final CardType[] PLAYER_CARD_TYPES = {CardType.PLAYER_CARD_1, CardType.PLAYER_CARD_2};
    public static final CardType[] FLOP_CARD_TYPES = {CardType.FLOP_CARD_1, CardType.FLOP_CARD_2, CardType.FLOP_CARD_3};
}
