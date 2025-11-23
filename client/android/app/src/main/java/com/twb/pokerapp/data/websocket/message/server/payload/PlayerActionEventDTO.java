package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.playeraction.PlayerActionDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;

import java.util.ArrayList;
import java.util.List;

public class PlayerActionEventDTO {
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
