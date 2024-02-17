package com.twb.pokerapp.data.websocket.message.client;

import androidx.annotation.NonNull;

public class SendPlayerActionDTO {
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @NonNull
    @Override
    public String toString() {
        return "SendPlayerActionDTO{" +
                "action='" + action + '\'' +
                '}';
    }
}
