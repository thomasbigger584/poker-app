package com.twb.pokergame.web.websocket.message.server.payload;

import lombok.Data;

@Data
public class PlayerDisconnectedDTO {
    private String username;
}
