package com.twb.pokerapp.data.model.dto.roundpot;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoundPotDTO {
    private UUID id;
    private Double potAmount;
    private Integer potIndex;
    private List<PlayerSessionDTO> eligiblePlayers = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getPotAmount() {
        return potAmount;
    }

    public void setPotAmount(Double potAmount) {
        this.potAmount = potAmount;
    }

    public Integer getPotIndex() {
        return potIndex;
    }

    public void setPotIndex(Integer potIndex) {
        this.potIndex = potIndex;
    }

    public List<PlayerSessionDTO> getEligiblePlayers() {
        return eligiblePlayers;
    }

    public void setEligiblePlayers(List<PlayerSessionDTO> eligiblePlayers) {
        this.eligiblePlayers = eligiblePlayers;
    }

    @NonNull
    @Override
    public String toString() {
        return "RoundPotDTO{" +
                "id=" + id +
                ", potAmount=" + potAmount +
                ", potIndex=" + potIndex +
                ", eligiblePlayers=" + eligiblePlayers +
                '}';
    }
}
