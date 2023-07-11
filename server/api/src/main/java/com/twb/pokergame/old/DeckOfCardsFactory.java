package com.twb.pokergame.old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckOfCardsFactory {
    private static final int[] RANKS = new int[]{Card.ACE, Card.KING, Card.QUEEN, Card.JACK, Card.TEN,
            Card.NINE, Card.EIGHT, Card.SEVEN, Card.SIX, Card.FIVE, Card.FOUR, Card.TREY, Card.DEUCE};
    private static final int[] SUITS = new int[]{Card.SPADES, Card.HEARTS, Card.DIAMONDS, Card.CLUBS};

    private static final List<Card> CARDS = new ArrayList<>();

    static {
        int valueIndex = 0;
        for (int rank : RANKS) {
            for (int suit : SUITS) {
                CARDS.add(new Card(rank, suit, valueIndex++));
            }
        }
    }

    //this will return a new instance of cards
    public static List<Card> getCards(boolean shuffle) {
        List<Card> deckOfCardsCopy = new ArrayList<>(CARDS);
        if (shuffle) {
            Collections.shuffle(deckOfCardsCopy);
        }
        return deckOfCardsCopy;
    }

    public static Card findCard(int rank, int suit) {
        for (Card card : CARDS) {
            if (card.getRank() == rank && card.getSuit() == suit) {
                return new Card(card);
            }
        }
        throw new RuntimeException("Failed to find card for rank: " + rank + " and suit: " + suit);
    }
}
