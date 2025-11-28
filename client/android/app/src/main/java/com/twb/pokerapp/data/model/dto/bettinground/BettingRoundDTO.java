package com.twb.pokerapp.data.model.dto.bettinground;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.card.CardDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BettingRoundDTO {
    private UUID id;
    private String state;
    private Double pot;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getPot() {
        return pot;
    }

    public void setPot(Double pot) {
        this.pot = pot;
    }

    @NonNull
    @Override
    public String toString() {
        return "BettingRoundDTO{" +
                "id=" + id +
                ", state='" + state + '\'' +
                ", pot=" + pot +
                '}';
    }
}
