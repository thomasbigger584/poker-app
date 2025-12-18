package com.twb.pokerapp.service.game.thread.impl.texas.dto;

import com.twb.pokerapp.domain.enumeration.ActionType;

public record NextActionsDTO(double amountToCall, ActionType[] nextActions) {
}
