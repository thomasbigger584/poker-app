package com.twb.pokerapp.data.model.dto.playeraction;

import com.twb.pokerapp.data.model.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;

import java.util.UUID;

public class PlayerActionDTO {

    private UUID id;
    private PlayerSessionDTO playerSession;
    private BettingRoundDTO bettingRound;
    private String actionType;
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

    public BettingRoundDTO getBettingRound() {
        return bettingRound;
    }

    public void setBettingRound(BettingRoundDTO bettingRound) {
        this.bettingRound = bettingRound;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "PlayerActionDTO{" +
                "id=" + id +
                ", playerSession=" + playerSession +
                ", bettingRound=" + bettingRound +
                ", actionType='" + actionType + '\'' +
                ", amount=" + amount +
                '}';
    }
}
