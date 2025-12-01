package com.twb.pokerapp.web.websocket.message.client;

import com.twb.pokerapp.domain.enumeration.ActionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreatePlayerActionDTO {

    @NotNull(message = "Action cannot be null")
    private ActionType action;

    @PositiveOrZero(message = "Amount provided must be positive or zero")
    private Double amount;
}
