package com.twb.pokerapp.data.websocket.message.client;

import androidx.annotation.NonNull;

public class SendPlayerActionDTO {
    private String action;
    private Double amount;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @NonNull
    @Override
    public String toString() {
        return "SendPlayerActionDTO{" +
                "action='" + action + '\'' +
                ", amount=" + amount +
                '}';
    }
}
