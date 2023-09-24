package com.twb.pokergame.web.websocket.message.server.payload;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String username;
    private String message;
}
