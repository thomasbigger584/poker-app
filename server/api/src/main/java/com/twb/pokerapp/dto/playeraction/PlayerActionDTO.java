package com.twb.pokerapp.dto.playeraction;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

import java.util.UUID;

@Data
public class PlayerActionDTO {
    private UUID id;
    private PlayerSessionDTO playerSession;
    private BettingRoundDTO bettingRound;
    private ActionType actionType;
    private Double amount;
}
