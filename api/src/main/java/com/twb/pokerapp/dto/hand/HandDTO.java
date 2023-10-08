package com.twb.pokerapp.dto.hand;


import com.twb.pokerapp.domain.enumeration.HandType;
import com.twb.pokerapp.dto.card.CardDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class HandDTO {
    private UUID id;
    private HandType handType;
    private String handTypeStr;
    private Boolean winner;
    private List<CardDTO> cards = new ArrayList<>();
}
