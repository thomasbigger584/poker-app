package com.twb.pokergame.data.model.dto.card;

import java.util.UUID;

public class CardDTO {
    private UUID id;
    private String rankType;
    private char rankChar;
    private int rankValue;
    private String suitType;
    private char suitChar;
    private String cardType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRankType() {
        return rankType;
    }

    public void setRankType(String rankType) {
        this.rankType = rankType;
    }

    public char getRankChar() {
        return rankChar;
    }

    public void setRankChar(char rankChar) {
        this.rankChar = rankChar;
    }

    public int getRankValue() {
        return rankValue;
    }

    public void setRankValue(int rankValue) {
        this.rankValue = rankValue;
    }

    public String getSuitType() {
        return suitType;
    }

    public void setSuitType(String suitType) {
        this.suitType = suitType;
    }

    public char getSuitChar() {
        return suitChar;
    }

    public void setSuitChar(char suitChar) {
        this.suitChar = suitChar;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public String toString() {
        return "CardDTO{" +
                "id=" + id +
                ", rankType='" + rankType + '\'' +
                ", rankChar=" + rankChar +
                ", rankValue=" + rankValue +
                ", suitType='" + suitType + '\'' +
                ", suitChar=" + suitChar +
                ", cardType='" + cardType + '\'' +
                '}';
    }
}
