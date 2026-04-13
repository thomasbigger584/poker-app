package com.twb.pokerapp.service.game.deck;

import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;

public class CardLibraryMapper {
    public static int toLibraryInt(RankType rank, SuitType suit) {
        var rankPos = rank.getPosition();
        var suitVal = suit.getValue();
        return (rankPos << 2) | (suitVal & 0x3);
    }
}
