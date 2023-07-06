package com.twb.pokergame.websocket.dto;

import lombok.Data;

@Data
public class WebSocketChatMessage {
    private String type;
    private String content;
    private String sender;
}
