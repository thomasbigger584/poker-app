package com.twb.pokerapp.data.websocket.message.server.payload;

import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;

import java.util.ArrayList;
import java.util.List;

public class PlayerSubscribedDTO {
    private List<PlayerSessionDTO> playerSessions = new ArrayList<>();

    public PlayerSessionDTO getCurrentPlayerSession(String username) {
        for (PlayerSessionDTO playerSession : playerSessions) {
            if (username.equals(playerSession.getUser().getUsername())) {
                return playerSession;
            }
        }
        throw new RuntimeException("Failed to find current player in sessions");
    }

    public List<PlayerSessionDTO> getPlayerSessions() {
        return playerSessions;
    }

    public void setPlayerSessions(List<PlayerSessionDTO> playerSessions) {
        this.playerSessions = playerSessions;
    }

    @Override
    public String toString() {
        return "PlayerSubscribedDTO{" +
                "playerSessions=" + playerSessions +
                '}';
    }
}
