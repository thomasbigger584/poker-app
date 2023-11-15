package com.twb.pokerapp.data.websocket.message.server.payload;

import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;

import java.util.ArrayList;
import java.util.List;

public class PlayerTurnDTO {
    private PlayerSessionDTO playerSession;
    private List<String> actions = new ArrayList<>();

    public PlayerSessionDTO getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(PlayerSessionDTO playerSession) {
        this.playerSession = playerSession;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "PlayerTurnDTO{" +
                "playerSession=" + playerSession +
                ", actions=" + actions +
                '}';
    }
}
