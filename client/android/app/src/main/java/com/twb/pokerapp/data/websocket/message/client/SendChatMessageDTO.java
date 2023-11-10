package com.twb.pokerapp.data.websocket.message.client;

import androidx.annotation.NonNull;

public class SendChatMessageDTO {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return "SendChatMessageDTO{" +
                "message='" + message + '\'' +
                '}';
    }
}
