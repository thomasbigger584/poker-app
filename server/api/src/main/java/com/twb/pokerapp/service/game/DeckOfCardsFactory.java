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
        var valueIndex = 0;
        for (var rank : RankType.values()) {
            for (var suit : SuitType.values()) {
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
        var deckOfCardsCopy = new ArrayList<>(CARDS);
        if (shuffle) {
            Collections.shuffle(deckOfCardsCopy);
        }
        return deckOfCardsCopy;
    }
}
