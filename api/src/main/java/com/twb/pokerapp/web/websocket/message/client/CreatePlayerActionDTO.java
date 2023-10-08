package com.twb.pokerapp.web.websocket.message.client;

import com.twb.pokerapp.domain.PlayerAction;
import lombok.Data;

@Data
public class CreatePlayerActionDTO {
    private PlayerAction action;
}
