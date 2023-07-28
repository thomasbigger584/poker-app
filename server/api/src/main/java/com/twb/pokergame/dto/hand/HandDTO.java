package com.twb.pokergame.dto.hand;


import com.twb.pokergame.domain.enumeration.HandType;
import com.twb.pokergame.dto.card.CardDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class HandDTO {
    private UUID id;
    private HandType handType;
    private String handTypeStr;
    private Boolean winner;
    private List<CardDTO> cards = new ArrayList<>();
}