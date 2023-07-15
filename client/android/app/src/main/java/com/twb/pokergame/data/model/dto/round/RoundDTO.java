package com.twb.pokergame.data.model.dto.round;

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
}
