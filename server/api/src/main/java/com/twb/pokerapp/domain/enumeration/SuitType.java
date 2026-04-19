package com.twb.pokerapp.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum SuitType {
    CLUBS(0, 'c'),
    DIAMONDS(1, 'd'),
    HEARTS(2, 'h'),
    SPADES(3, 's');

    private final int value;
    private final char suitChar;

    public static SuitType fromSuitChar(char suitChar) {
        for (var suitType : values()) {
            if (suitType.getSuitChar() == suitChar) {
                return suitType;
            }
        }
        throw new IllegalArgumentException("Invalid suit character: " + suitChar);
    }
}
