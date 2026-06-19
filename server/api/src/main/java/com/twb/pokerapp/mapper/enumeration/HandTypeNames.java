package com.twb.pokerapp.mapper.enumeration;

import com.twb.pokerapp.proto.HandType;

import java.util.EnumMap;
import java.util.Map;

import static com.twb.pokerapp.proto.HandType.*;

/**
 * Human-readable display names for hand types (e.g. {@code "Royal Flush"}). Extracted from the former
 * {@code HandType} domain enum's value field.
 */
public final class HandTypeNames {

    private static final Map<HandType, String> NAMES = new EnumMap<>(HandType.class);

    static {
        NAMES.put(HAND_TYPE_ROYAL_FLUSH, "Royal Flush");
        NAMES.put(HAND_TYPE_STRAIGHT_FLUSH, "Straight Flush");
        NAMES.put(HAND_TYPE_FOUR_OF_A_KIND, "Four of a Kind");
        NAMES.put(HAND_TYPE_FULL_HOUSE, "Full House");
        NAMES.put(HAND_TYPE_FLUSH, "Flush");
        NAMES.put(HAND_TYPE_STRAIGHT, "Straight");
        NAMES.put(HAND_TYPE_THREE_OF_A_KIND, "Three of a Kind");
        NAMES.put(HAND_TYPE_TWO_PAIR, "Two Pair");
        NAMES.put(HAND_TYPE_PAIR, "Pair");
        NAMES.put(HAND_TYPE_HIGH_CARD, "High Card");
        NAMES.put(HAND_TYPE_EMPTY_HAND, "Empty Hand");
    }

    private HandTypeNames() {
    }

    /**
     * The display name for a hand type, or {@code ""} for an unspecified/unknown value.
     */
    public static String displayName(HandType handType) {
        return handType == null ? "" : NAMES.getOrDefault(handType, "");
    }
}
