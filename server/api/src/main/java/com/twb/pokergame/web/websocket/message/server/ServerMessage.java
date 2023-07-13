package com.twb.pokergame.web.websocket.message.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerMessage {
    private ServerMessageType type;
    private long timestamp;
    private Object body;

    public static ServerMessage create(ServerMessageType type, Object body) {
        return ServerMessage.builder()
                .type(type)
                .timestamp(System.currentTimeMillis())
                .body(body)
                .build();
    }
}
