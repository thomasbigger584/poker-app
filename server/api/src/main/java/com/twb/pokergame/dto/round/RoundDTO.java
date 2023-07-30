package com.twb.pokergame.dto.round;

import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.dto.card.CardDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class RoundDTO {
    private UUID id;
    private RoundState roundState;
    private List<CardDTO> communityCards = new ArrayList<>();
}
