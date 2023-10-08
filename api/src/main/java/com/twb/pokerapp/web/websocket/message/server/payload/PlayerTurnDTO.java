package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.domain.enumeration.ActionType;
import lombok.Data;

@Data
public class PlayerTurnDTO {
    private String username;
    private ActionType[] actions;
}
