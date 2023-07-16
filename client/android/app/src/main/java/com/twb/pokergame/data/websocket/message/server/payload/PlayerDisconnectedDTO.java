package com.twb.pokergame.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

public class PlayerDisconnectedDTO {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayerDisconnectedDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}
