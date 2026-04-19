package com.twb.pokerapp.data.model.dto.roundwinner;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.hand.HandDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.data.model.dto.round.RoundDTO;

import java.util.UUID;

public class RoundWinnerDTO {
    private UUID id;
    private PlayerSessionDTO playerSession;
    private RoundDTO round;
    private HandDTO hand;
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

    public RoundDTO getRound() {
        return round;
    }

    public void setRound(RoundDTO round) {
        this.round = round;
    }

    public HandDTO getHand() {
        return hand;
    }

    public void setHand(HandDTO hand) {
        this.hand = hand;
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
        return "RoundWinnerDTO{" +
                "id=" + id +
                ", playerSession=" + playerSession +
                ", round=" + round +
                ", hand=" + hand +
                ", amount=" + amount +
                '}';
    }
}
