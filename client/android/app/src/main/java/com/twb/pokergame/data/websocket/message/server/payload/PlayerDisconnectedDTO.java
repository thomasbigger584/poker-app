package com.twb.pokergame.data.websocket.message.server.payload;

public class PlayerDisconnectedDTO {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "PlayerDisconnectedDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}
