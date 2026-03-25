package com.twb.pokerapp.service.game.deck;

import com.google.common.annotations.VisibleForTesting;
import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
import com.twb.pokerapp.service.game.deck.shuffler.Shuffler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating a deck of cards.
 */
@Component
@RequiredArgsConstructor
public class DeckFactory {
    private final Shuffler shuffler;

    @VisibleForTesting
    public static final List<Card> CARDS = new ArrayList<>();

    static {
        var valueIndex = 0;
        for (var suit : SuitType.values()) {
            for (var rank : RankType.values()) {
                CARDS.add(new Card(rank, suit, valueIndex++));
            }
        }
    }

    /**
     * Returns a new instance of a deck of cards.
     *
     * @return a list of cards representing the shuffled deck
     */
    public List<Card> getShuffledDeck() {
        return shuffler.shuffle(CARDS);
    }
}
