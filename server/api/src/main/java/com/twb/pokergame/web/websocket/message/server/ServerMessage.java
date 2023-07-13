package com.twb.pokergame.web.websocket.message.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerMessage {
    private ServerMessageType type;
    private Object body;
}
