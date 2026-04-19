package com.twb.pokerapp.service.game.thread.impl.texas.dto;

import com.twb.pokerapp.domain.enumeration.ActionType;

import java.math.BigDecimal;

public record NextActionsDTO(BigDecimal amountToCall, ActionType[] nextActions) {
}
