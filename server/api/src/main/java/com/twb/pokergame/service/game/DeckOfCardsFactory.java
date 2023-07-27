package com.twb.pokergame.service.game;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.enumeration.RankType;
import com.twb.pokergame.domain.enumeration.SuitType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    //this will return a new instance of cards
    public static List<Card> getCards(boolean shuffle) {
        List<Card> deckOfCardsCopy = new ArrayList<>(CARDS);
        if (shuffle) {
            Collections.shuffle(deckOfCardsCopy);
        }
        return deckOfCardsCopy;
    }
}
