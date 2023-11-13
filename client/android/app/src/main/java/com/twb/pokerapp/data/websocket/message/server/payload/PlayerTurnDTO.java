package com.twb.pokerapp.data.websocket.message.server.payload;

import java.util.ArrayList;
import java.util.List;

public class PlayerTurnDTO {
    private String username;
    private List<String> actions = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
                "username='" + username + '\'' +
                ", actions=" + actions +
                '}';
    }
}
