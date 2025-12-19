package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.data.model.dto.round.RoundDTO;

public class BettingRoundUpdatedDTO {
    private RoundDTO round;
    private BettingRoundDTO bettingRound;

    public RoundDTO getRound() {
        return round;
    }

    public void setRound(RoundDTO round) {
        this.round = round;
    }

    public BettingRoundDTO getBettingRound() {
        return bettingRound;
    }

    public void setBettingRound(BettingRoundDTO bettingRound) {
        this.bettingRound = bettingRound;
    }

    @NonNull
    @Override
    public String toString() {
        return "BettingRoundUpdatedDTO{" +
                "round=" + round +
                ", bettingRound=" + bettingRound +
                '}';
    }
}
