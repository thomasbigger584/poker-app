package com.twb.pokerapp.service.game;

import com.google.common.annotations.VisibleForTesting;
import com.twb.pokerapp.configuration.ProfileConfiguration;
import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory class for creating a deck of cards.
 */
@Component
@RequiredArgsConstructor
public class DeckOfCardsFactory {
    private final ProfileConfiguration profileConfiguration;

    @VisibleForTesting
    public static final List<Card> CARDS = new ArrayList<>();

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
    public List<Card> getCards(boolean shuffle) {
        var deckOfCardsCopy = new ArrayList<>(CARDS);
        if (shuffle) {
            Collections.shuffle(deckOfCardsCopy);
        }
        if (!profileConfiguration.hasTestProfile()) {
            return deckOfCardsCopy;
        }
        /* TODO: get all test cards from database
         *  add those as Cards to the top of the list and add the rest to make a full deck
         */
        return deckOfCardsCopy;
    }
}
