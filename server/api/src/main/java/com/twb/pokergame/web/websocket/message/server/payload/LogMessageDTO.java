package com.twb.pokergame.web.websocket.message.server.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogMessageDTO {
    private String message;
}