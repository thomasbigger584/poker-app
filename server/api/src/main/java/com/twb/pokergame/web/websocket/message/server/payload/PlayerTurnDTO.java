package com.twb.pokergame.web.websocket.message.server.payload;

import com.twb.pokergame.domain.enumeration.ActionType;
import lombok.Data;

@Data
public class PlayerTurnDTO {
    private String username;
    private ActionType[] actions;
}
