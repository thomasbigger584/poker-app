package com.twb.pokergame.data.model;

public class Card {
    private static final byte[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};
    private static final String RANKS = "23456789TJQKA";
    private static final String SUITS = "shdc";
    private final int rank;
    private final int suit;
    private final int value;  // Format: xxxAKQJT 98765432 CDHSrrrr xxPPPPPP
    private final int rankValue;

    /**
     * Creates a new card with the given rank and suit.
     */
    public Card(int rank, int suit, int rankValue) {
        this.rank = rank;
        this.suit = suit;
        this.value = (1 << (rank + 16)) | suit | (rank << 8) | PRIMES[rank];
        if (rankValue < 0 || rankValue > 51) {
            throw new IllegalArgumentException("Invalid Rank Value");
        }
        this.rankValue = rankValue;
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
