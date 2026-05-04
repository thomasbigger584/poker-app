package com.twb.pokerapp.dto.appuser;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserAmountDTO {
    @NotNull
    @Positive
    private BigDecimal amount;
}
