package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;

public class DealerDeterminedDTO {
    private PlayerSessionDTO playerSession;

    public PlayerSessionDTO getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(PlayerSessionDTO playerSession) {
        this.playerSession = playerSession;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayerConnectedDTO{" +
                "playerSession=" + playerSession +
                '}';
    }
}
