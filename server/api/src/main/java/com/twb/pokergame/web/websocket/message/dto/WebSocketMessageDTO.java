package com.twb.pokergame.web.websocket.message.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebSocketMessageDTO {
    private MessageType type;
    private String content;
    private String sender;
}
