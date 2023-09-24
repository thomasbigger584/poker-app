package com.twb.pokergame.dto.card;


import com.twb.pokergame.domain.enumeration.CardType;
import com.twb.pokergame.domain.enumeration.RankType;
import com.twb.pokergame.domain.enumeration.SuitType;
import lombok.Data;

import java.util.UUID;

@Data
public class CardDTO {
    private UUID id;
    private RankType rankType;
    private char rankChar;
    private int rankValue;
    private SuitType suitType;
    private char suitChar;
    private CardType cardType;
}
