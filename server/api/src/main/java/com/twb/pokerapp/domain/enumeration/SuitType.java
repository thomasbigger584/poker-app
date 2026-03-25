package com.twb.pokerapp.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum SuitType {
    CLUBS(0x8000, 'c'),
    DIAMONDS(0x4000, 'd'),
    HEARTS(0x2000, 'h'),
    SPADES(0x1000, 's');

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
