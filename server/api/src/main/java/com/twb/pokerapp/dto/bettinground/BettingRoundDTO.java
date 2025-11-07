package com.twb.pokerapp.dto.bettinground;

import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.dto.card.CardDTO;
import com.twb.pokerapp.dto.playeraction.PlayerActionDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class BettingRoundDTO {
    private UUID id;
    private BettingRoundState bettingRoundState;
    private Double pot;
    private List<PlayerActionDTO> playerActions = new ArrayList<>();
}
