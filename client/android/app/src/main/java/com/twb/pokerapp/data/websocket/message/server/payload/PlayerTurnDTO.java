package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.playeraction.PlayerActionDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;

import java.util.ArrayList;
import java.util.List;

public class PlayerTurnDTO {
    private PlayerSessionDTO playerSession;
    private PlayerActionDTO prevPlayerAction;
    private List<String> nextActions = new ArrayList<>();
    private Double amountToCall;

    public PlayerSessionDTO getPlayerSession() {
        return playerSession;
    }

    public void setPlayerSession(PlayerSessionDTO playerSession) {
        this.playerSession = playerSession;
    }

    public PlayerActionDTO getPrevPlayerAction() {
        return prevPlayerAction;
    }

    public void setPrevPlayerAction(PlayerActionDTO prevPlayerAction) {
        this.prevPlayerAction = prevPlayerAction;
    }

    public List<String> getNextActions() {
        return nextActions;
    }

    public void setNextActions(List<String> nextActions) {
        this.nextActions = nextActions;
    }

    public Double getAmountToCall() {
        return amountToCall;
    }

    public void setAmountToCall(Double amountToCall) {
        this.amountToCall = amountToCall;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayerTurnDTO{" +
                "playerSession=" + playerSession +
                ", prevPlayerAction=" + prevPlayerAction +
                ", actions=" + nextActions +
                ", amountToCall=" + amountToCall +
                '}';
    }
}
