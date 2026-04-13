package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.data.model.dto.round.RoundDTO;
import com.twb.pokerapp.data.model.dto.roundpot.RoundPotDTO;

import java.util.ArrayList;
import java.util.List;

public class BettingRoundUpdatedDTO {
    private RoundDTO round;
    private BettingRoundDTO bettingRound;
    private List<RoundPotDTO> roundPots = new ArrayList<>();

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

    public List<RoundPotDTO> getRoundPots() {
        return roundPots;
    }

    public void setRoundPots(List<RoundPotDTO> roundPots) {
        this.roundPots = roundPots;
    }

    @NonNull
    @Override
    public String toString() {
        return "BettingRoundUpdatedDTO{" +
                "round=" + round +
                ", bettingRound=" + bettingRound +
                ", roundPots=" + roundPots +
                '}';
    }
}
