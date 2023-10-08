package com.twb.pokerapp.data.model.dto.round;

import androidx.annotation.NonNull;

import java.util.UUID;

public class RoundDTO {
    private UUID id;
    private String roundState;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRoundState() {
        return roundState;
    }

    public void setRoundState(String roundState) {
        this.roundState = roundState;
    }

    @NonNull
    @Override
    public String toString() {
        return "RoundDTO{" +
                "id=" + id +
                ", roundState='" + roundState + '\'' +
                '}';
    }
}
