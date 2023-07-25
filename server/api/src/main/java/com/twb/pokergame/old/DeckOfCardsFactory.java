package com.twb.pokergame.old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckOfCardsFactory {
    private static final int[] RANKS = new int[]{CardDTO.ACE, CardDTO.KING, CardDTO.QUEEN, CardDTO.JACK, CardDTO.TEN,
            CardDTO.NINE, CardDTO.EIGHT, CardDTO.SEVEN, CardDTO.SIX, CardDTO.FIVE, CardDTO.FOUR, CardDTO.TREY, CardDTO.DEUCE};
    private static final int[] SUITS = new int[]{CardDTO.SPADES, CardDTO.HEARTS, CardDTO.DIAMONDS, CardDTO.CLUBS};

    private static final List<CardDTO> CARDS = new ArrayList<>();

    static {
        int valueIndex = 0;
        for (int rank : RANKS) {
            for (int suit : SUITS) {
                CARDS.add(new CardDTO(rank, suit, valueIndex++));
            }
        }
    }

    //this will return a new instance of cards
    public static List<CardDTO> getCards(boolean shuffle) {
        List<CardDTO> deckOfCardsCopy = new ArrayList<>(CARDS);
        if (shuffle) {
            Collections.shuffle(deckOfCardsCopy);
        }
        return deckOfCardsCopy;
    }

    public static CardDTO findCard(int rank, int suit) {
        for (CardDTO card : CARDS) {
            if (card.getRank() == rank && card.getSuit() == suit) {
                return new CardDTO(card);
            }
        }
        throw new RuntimeException("Failed to find card for rank: " + rank + " and suit: " + suit);
    }
}
