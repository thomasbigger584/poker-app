package com.twb.pokergame.data.model.enumeration;

public enum CardType {
    PLAYER_CARD_1,
    PLAYER_CARD_2,
    FLOP_CARD_1,
    FLOP_CARD_2,
    FLOP_CARD_3,
    TURN_CARD,
    RIVER_CARD;

    public static final CardType[] PLAYER_CARDS = {PLAYER_CARD_1, PLAYER_CARD_2};
    public static final CardType[] FLOP_CARDS = {FLOP_CARD_1, FLOP_CARD_2, FLOP_CARD_3};
}
