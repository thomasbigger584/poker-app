package com.twb.pokergame.web.websocket.message.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerMessage {
    private ServerMessageType type;
    private long timestamp;
    private Object payload;

    public static ServerMessage create(ServerMessageType type, Object payload) {
        return ServerMessage.builder()
                .type(type)
                .timestamp(System.currentTimeMillis())
                .payload(payload)
                .build();
    }
}
