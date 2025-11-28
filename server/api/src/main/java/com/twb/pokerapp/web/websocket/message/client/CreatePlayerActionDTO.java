package com.twb.pokerapp.web.websocket.message.client;

import com.twb.pokerapp.domain.enumeration.ActionType;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePlayerActionDTO {
    private ActionType action;
    private Double amount;
}
