package com.twb.pokergame.web.websocket.dto;

import lombok.Data;

@Data
public class WebSocketChatMessage {
    private String type;
    private String content;
    private String sender;
}
