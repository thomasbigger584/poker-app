package com.twb.pokergame.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum SuitType {
    SPADES(0x1000),
    HEARTS(0x2000),
    DIAMONDS(0x4000),
    CLUBS(0x8000);

    private final int value;
}
