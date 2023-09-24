package com.twb.pokergame.web.websocket.message.client;

import com.twb.pokergame.domain.PlayerAction;
import lombok.Data;

@Data
public class CreatePlayerActionDTO {
    private PlayerAction action;
}
