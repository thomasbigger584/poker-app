package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.playeraction.PlayerActionDTO;
import lombok.Data;

@Data
public class PlayerActionEventDTO {
    private PlayerActionDTO action;
}
