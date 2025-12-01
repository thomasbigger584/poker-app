package com.twb.pokerapp.web.websocket.message.server.payload.validation;

import lombok.Data;

import java.util.List;

@Data
public class ValidationDTO {
    private List<ValidationFieldDTO> fields;
}
