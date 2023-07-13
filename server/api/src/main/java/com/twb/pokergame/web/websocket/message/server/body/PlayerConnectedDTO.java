package com.twb.pokergame.web.websocket.message.server.body;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerConnectedDTO {
    private String username;
}
