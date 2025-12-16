package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.data.model.dto.playeraction.PlayerActionDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;

import java.util.ArrayList;
import java.util.List;

public class PlayerTurnDTO {
    private PlayerSessionDTO playerSession;
    private PlayerActionDTO prevPlayerAction;
    private BettingRoundDTO bettingRound;
    private List<String> nextActions = new ArrayList<>();
    private Double amountToCall;
    private Long playerTurnWaitMs;

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

    public BettingRoundDTO getBettingRound() {
        return bettingRound;
    }

    public void setBettingRound(BettingRoundDTO bettingRound) {
        this.bettingRound = bettingRound;
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

    public Long getPlayerTurnWaitMs() {
        return playerTurnWaitMs;
    }

    public void setPlayerTurnWaitMs(Long playerTurnWaitMs) {
        this.playerTurnWaitMs = playerTurnWaitMs;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayerTurnDTO{" +
                "playerSession=" + playerSession +
                ", prevPlayerAction=" + prevPlayerAction +
                ", bettingRound=" + bettingRound +
                ", actions=" + nextActions +
                ", amountToCall=" + amountToCall +
                ", playerTurnWaitMs=" + playerTurnWaitMs +
                '}';
    }
}
