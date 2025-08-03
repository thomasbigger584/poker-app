package com.twb.pokerapp.dto.playeraction;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.dto.round.RoundDTO;
import lombok.Data;

import java.util.UUID;

@Data
public class PlayerActionDTO {
    private UUID id;
    private PlayerSessionDTO playerSession;
    private RoundDTO round;
    private RoundState roundState;
    private ActionType actionType;
    private Double amount;
}
