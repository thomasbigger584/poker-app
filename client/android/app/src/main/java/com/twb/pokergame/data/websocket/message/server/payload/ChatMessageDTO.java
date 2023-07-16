package com.twb.pokergame.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

public class ChatMessageDTO {
    private String username;
    private String message;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
