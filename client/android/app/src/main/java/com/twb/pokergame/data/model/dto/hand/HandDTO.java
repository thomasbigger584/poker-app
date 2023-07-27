package com.twb.pokergame.data.model.dto.hand;

import androidx.annotation.NonNull;

import com.twb.pokergame.data.model.dto.card.CardDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HandDTO {
    private UUID id;
    private String handType;
    private String handTypeStr;
    private Boolean winner;
    private List<CardDTO> cards = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getHandType() {
        return handType;
    }

    public void setHandType(String handType) {
        this.handType = handType;
    }

    public String getHandTypeStr() {
        return handTypeStr;
    }

    public void setHandTypeStr(String handTypeStr) {
        this.handTypeStr = handTypeStr;
    }

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }

    public List<CardDTO> getCards() {
        return cards;
    }

    public void setCards(List<CardDTO> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public String toString() {
        return "HandDTO{" +
                "id=" + id +
                ", handType='" + handType + '\'' +
                ", handTypeStr='" + handTypeStr + '\'' +
                ", winner=" + winner +
                ", cards=" + cards +
                '}';
    }
}
