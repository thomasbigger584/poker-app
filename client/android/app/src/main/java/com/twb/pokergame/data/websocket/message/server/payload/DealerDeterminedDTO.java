package com.twb.pokergame.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokergame.data.model.dto.playersession.PlayerSessionDTO;

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
