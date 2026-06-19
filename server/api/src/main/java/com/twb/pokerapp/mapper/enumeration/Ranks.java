package com.twb.pokerapp.mapper.enumeration;

import com.twb.pokerapp.proto.RankType;

import java.util.EnumMap;
import java.util.Map;

import static com.twb.pokerapp.proto.RankType.*;

/**
 * Card-rank metadata that is not part of the proto wire enum: the single-character code used by the
 * fixed-scenario deck notation and the native hand evaluator, and the zero-based position used to
 * encode a card for that evaluator. Extracted from the former {@code RankType} domain enum.
 */
public final class Ranks {

    /**
     * The 13 real ranks, low to high, excluding the proto {@code UNSPECIFIED} / {@code UNRECOGNIZED}.
     */
    public static final RankType[] VALUES = {
            RANK_TYPE_DEUCE, RANK_TYPE_TREY, RANK_TYPE_FOUR, RANK_TYPE_FIVE, RANK_TYPE_SIX,
            RANK_TYPE_SEVEN, RANK_TYPE_EIGHT, RANK_TYPE_NINE, RANK_TYPE_TEN, RANK_TYPE_JACK,
            RANK_TYPE_QUEEN, RANK_TYPE_KING, RANK_TYPE_ACE
    };

    private static final Map<RankType, Character> CHARS = new EnumMap<>(RankType.class);

    static {
        CHARS.put(RANK_TYPE_DEUCE, '2');
        CHARS.put(RANK_TYPE_TREY, '3');
        CHARS.put(RANK_TYPE_FOUR, '4');
        CHARS.put(RANK_TYPE_FIVE, '5');
        CHARS.put(RANK_TYPE_SIX, '6');
        CHARS.put(RANK_TYPE_SEVEN, '7');
        CHARS.put(RANK_TYPE_EIGHT, '8');
        CHARS.put(RANK_TYPE_NINE, '9');
        CHARS.put(RANK_TYPE_TEN, 't');
        CHARS.put(RANK_TYPE_JACK, 'j');
        CHARS.put(RANK_TYPE_QUEEN, 'q');
        CHARS.put(RANK_TYPE_KING, 'k');
        CHARS.put(RANK_TYPE_ACE, 'a');
    }

    private Ranks() {
    }

    /**
     * Zero-based rank index (DEUCE=0 .. ACE=12). The proto number is 1-based, so subtract one.
     */
    public static int position(RankType rank) {
        return rank.getNumber() - 1;
    }

    public static char charOf(RankType rank) {
        return CHARS.get(rank);
    }

    public static RankType fromChar(char rankChar) {
        for (var entry : CHARS.entrySet()) {
            if (entry.getValue() == rankChar) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Invalid rank character: " + rankChar);
    }
}
