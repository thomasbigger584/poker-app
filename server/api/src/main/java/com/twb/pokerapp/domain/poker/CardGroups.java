package com.twb.pokerapp.domain.poker;

import com.twb.pokerapp.proto.CardType;

import static com.twb.pokerapp.proto.CardType.*;

/**
 * The card-type groupings dealt together during a Texas Hold'em round. Extracted from the former
 * {@code CardType} domain enum constants.
 */
public final class CardGroups {

    /** The two hole cards dealt to each player. */
    public static final CardType[] PLAYER_CARDS = {CARD_TYPE_PLAYER_CARD_1, CARD_TYPE_PLAYER_CARD_2};

    /** The three community cards dealt on the flop. */
    public static final CardType[] FLOP_CARDS = {CARD_TYPE_FLOP_CARD_1, CARD_TYPE_FLOP_CARD_2, CARD_TYPE_FLOP_CARD_3};

    private CardGroups() {
    }
}
