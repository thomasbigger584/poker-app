package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.playeraction.PlayerActionDTO;

public class PlayerActionedDTO {
    private PlayerActionDTO action;

    public PlayerActionDTO getAction() {
        return action;
    }

    public void setAction(PlayerActionDTO action) {
        this.action = action;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayerActionEventDTO{" +
                "action=" + action +
                '}';
    }
}
