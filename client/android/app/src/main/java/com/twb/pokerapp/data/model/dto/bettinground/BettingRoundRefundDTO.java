package com.twb.pokerapp.data.model.dto.bettinground;


import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;

import java.util.UUID;

public class BettingRoundRefundDTO {
    private UUID id;
    private PlayerSessionDTO playerSession;
    private Double amount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PlayerSessionDTO getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(PlayerSessionDTO playerSession) {
        this.playerSession = playerSession;
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
        return "BettingRoundRefundDTO{" +
                "id=" + id +
                ", playerSession=" + playerSession +
                ", amount=" + amount +
                '}';
    }
}
