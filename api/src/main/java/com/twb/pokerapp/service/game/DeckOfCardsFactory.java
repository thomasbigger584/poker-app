package com.twb.pokerapp.service.game;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory class for creating a deck of cards.
 */
public class DeckOfCardsFactory {
    private static final List<Card> CARDS = new ArrayList<>();

    static {
        int valueIndex = 0;
        for (RankType rank : RankType.values()) {
            for (SuitType suit : SuitType.values()) {
                CARDS.add(new Card(rank, suit, valueIndex++));
            }
        }
    }

    /**
     * Returns a new instance of a deck of cards.
     *
     * @param shuffle if true, the deck will be shuffled
     * @return a list of cards representing the deck
     */
    public static List<Card> getCards(boolean shuffle) {
        List<Card> deckOfCardsCopy = new ArrayList<>(CARDS);
        if (shuffle) {
            Collections.shuffle(deckOfCardsCopy);
        }
        return deckOfCardsCopy;
    }
}
