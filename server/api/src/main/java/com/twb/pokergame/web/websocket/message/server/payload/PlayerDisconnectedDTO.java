package com.twb.pokergame.web.websocket.message.server.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerDisconnectedDTO {
    private String username;
}