package com.twb.pokerapp.data.model.dto.bettinground;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BettingRoundDTO {
    private UUID id;
    private String type;
    private String state;
    private List<BettingRoundRefundDTO> bettingRoundRefunds = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<BettingRoundRefundDTO> getBettingRoundRefunds() {
        return bettingRoundRefunds;
    }

    public void setBettingRoundRefunds(List<BettingRoundRefundDTO> bettingRoundRefunds) {
        this.bettingRoundRefunds = bettingRoundRefunds;
    }

    @NonNull
    @Override
    public String toString() {
        return "BettingRoundDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                ", bettingRoundRefunds=" + bettingRoundRefunds +
                '}';
    }
}
