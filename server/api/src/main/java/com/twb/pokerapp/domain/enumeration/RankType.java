package com.twb.pokerapp.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Getter
@ToString
@RequiredArgsConstructor
public enum RankType {
    DEUCE(0, '2'),
    TREY(1, '3'),
    FOUR(2, '4'),
    FIVE(3, '5'),
    SIX(4, '6'),
    SEVEN(5, '7'),
    EIGHT(6, '8'),
    NINE(7, '9'),
    TEN(8, 't'),
    JACK(9, 'j'),
    QUEEN(10, 'q'),
    KING(11, 'k'),
    ACE(12, 'a');

    private final int position;
    private final char rankChar;
}
