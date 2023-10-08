package com.twb.pokerapp.dto.card;


import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
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
