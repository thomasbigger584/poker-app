package com.twb.pokergame.data.model;

import com.twb.pokergame.data.model.dto.CardDTO;

public class Card {
    private static final String RANKS = "23456789TJQKA";
    private static final String SUITS = "shdc";
    private final int rank;
    private final int suit;

    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Card(CardDTO dto) {
        this(dto.getRank(), dto.getSuit());
    }

    public String getDrawable() {
        char rank = RANKS.charAt(getRank());
        char suit = SUITS.charAt((int) (Math.log(getSuit()) / Math.log(2)) - 12);
        String drawableName = String.valueOf(suit) + rank;
        return drawableName.toLowerCase();
    }

    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }
}
