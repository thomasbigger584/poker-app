package com.twb.pokergame.web.websocket.message.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerMessageDTO {
    private ServerMessageType type;
    private long timestamp;
    private Object payload;

    public static ServerMessageDTO create(ServerMessageType type, Object payload) {
        return ServerMessageDTO.builder()
                .type(type)
                .timestamp(System.currentTimeMillis())
                .payload(payload)
                .build();
    }

    public static ServerMessageDTO create(ServerMessageType type, long timestamp, Object payload) {
        return ServerMessageDTO.builder()
                .type(type)
                .timestamp(timestamp)
                .payload(payload)
                .build();
    }
}
