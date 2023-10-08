package com.twb.pokerapp.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum SuitType {
    SPADES(0x1000, 's'),
    HEARTS(0x2000, 'h'),
    DIAMONDS(0x4000, 'd'),
    CLUBS(0x8000, 'c');

    private final int value;
    private final char suitChar;
}
