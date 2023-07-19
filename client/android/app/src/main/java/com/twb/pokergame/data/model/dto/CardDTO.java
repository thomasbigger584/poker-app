package com.twb.pokergame.data.model.dto;

public class CardDTO {
    private int suit;
    private int rank;

    public int getSuit() {
        return suit;
    }

    public void setSuit(int suit) {
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }


    @Override
    public String toString() {
        return "CardDTO{" +
                "suit=" + suit +
                ", rank=" + rank +
                '}';
    }
}
