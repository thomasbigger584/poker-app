package com.twb.pokerapp.web.websocket.message.client;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateBotConnectionDTO {

    @NotNull(message = "Bot User ID cannot be null")
    private UUID botUserId;

    @NotNull(message = "Buy-In amount cannot be null")
    private BigDecimal buyInAmount;
}
