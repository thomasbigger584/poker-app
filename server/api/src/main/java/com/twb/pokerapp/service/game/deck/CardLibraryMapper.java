package com.twb.pokerapp.service.game.deck;

import com.twb.pokerapp.domain.poker.Ranks;
import com.twb.pokerapp.domain.poker.Suits;
import com.twb.pokerapp.proto.RankType;
import com.twb.pokerapp.proto.SuitType;

public class CardLibraryMapper {
    public static int toLibraryInt(RankType rank, SuitType suit) {
        var rankPos = Ranks.position(rank);
        var suitVal = Suits.value(suit);
        return (rankPos << 2) | (suitVal & 0x3);
    }
}
