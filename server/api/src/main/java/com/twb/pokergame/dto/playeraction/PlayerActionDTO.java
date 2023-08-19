package com.twb.pokergame.dto.playeraction;

import com.twb.pokergame.domain.enumeration.ActionType;
import com.twb.pokergame.domain.enumeration.RoundState;
import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.dto.round.RoundDTO;
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
