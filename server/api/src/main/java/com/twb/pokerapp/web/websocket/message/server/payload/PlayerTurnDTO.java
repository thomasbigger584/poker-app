package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

@Data
public class PlayerTurnDTO {
    private PlayerSessionDTO playerSession;
    private ActionType[] actions;
    private Double amountToCall;
}
