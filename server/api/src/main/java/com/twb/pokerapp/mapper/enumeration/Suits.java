package com.twb.pokerapp.mapper.enumeration;

import com.twb.pokerapp.proto.SuitType;

import java.util.EnumMap;
import java.util.Map;

import static com.twb.pokerapp.proto.SuitType.*;

/**
 * Card-suit metadata that is not part of the proto wire enum: the single-character code used by the
 * fixed-scenario deck notation and the zero-based value used to encode a card for the native hand
 * evaluator. Extracted from the former {@code SuitType} domain enum.
 */
public final class Suits {

    /**
     * The four real suits, excluding the proto {@code UNSPECIFIED} / {@code UNRECOGNIZED}.
     */
    public static final SuitType[] VALUES = {
            SUIT_TYPE_CLUBS, SUIT_TYPE_DIAMONDS, SUIT_TYPE_HEARTS, SUIT_TYPE_SPADES
    };

    private static final Map<SuitType, Character> CHARS = new EnumMap<>(SuitType.class);

    static {
        CHARS.put(SUIT_TYPE_CLUBS, 'c');
        CHARS.put(SUIT_TYPE_DIAMONDS, 'd');
        CHARS.put(SUIT_TYPE_HEARTS, 'h');
        CHARS.put(SUIT_TYPE_SPADES, 's');
    }

    private Suits() {
    }

    /**
     * Zero-based suit value (CLUBS=0 .. SPADES=3). The proto number is 1-based, so subtract one.
     */
    public static int value(SuitType suit) {
        return suit.getNumber() - 1;
    }

    public static char charOf(SuitType suit) {
        return CHARS.get(suit);
    }

    public static SuitType fromChar(char suitChar) {
        for (var entry : CHARS.entrySet()) {
            if (entry.getValue() == suitChar) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Invalid suit character: " + suitChar);
    }
}
