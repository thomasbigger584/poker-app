package com.twb.pokerapp.dto.round;

import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.dto.card.CardDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class RoundDTO {
    private UUID id;
    private RoundState roundState;
    private Double pot;
    private List<CardDTO> communityCards = new ArrayList<>();
}
