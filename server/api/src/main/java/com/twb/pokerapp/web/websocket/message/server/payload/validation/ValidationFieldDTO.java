package com.twb.pokerapp.web.websocket.message.server.payload.validation;

import lombok.Data;

@Data
public class ValidationFieldDTO {
    private String field;
    private String message;
}
