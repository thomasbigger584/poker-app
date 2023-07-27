package com.twb.pokergame.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Getter
@ToString
@RequiredArgsConstructor
public enum RankType {
    DEUCE(0),
    TREY(1),
    FOUR(2),
    FIVE(3),
    SIX(4),
    SEVEN(5),
    EIGHT(6),
    NINE(7),
    TEN(8),
    JACK(9),
    QUEEN(10),
    KING(11),
    ACE(12);

    private final int position;

    public static Optional<RankType> findRankByPosition(int position) {
        for (RankType rankType : values()) {
            if (rankType.getPosition() == position) {
                return Optional.of(rankType);
            }
        }
        return Optional.empty();
    }

}
