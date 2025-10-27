package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
import com.twb.pokerapp.exception.NotFoundException;
import com.twb.pokerapp.service.game.DeckOfCardsFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HandFixture {

    private static final Map<String, Card> DECK_MAP = DeckOfCardsFactory.getCards(false).stream()
            .collect(Collectors.toUnmodifiableMap(card -> card.getRankType().toString() + card.getSuitType().toString(), Function.identity()));

    public static List<Card> createRoyalFlush() {
        SuitType suit = SuitType.HEARTS;
        return List.of(
                findCard(RankType.TEN, suit),
                findCard(RankType.JACK, suit),
                findCard(RankType.QUEEN, suit),
                findCard(RankType.KING, suit),
                findCard(RankType.ACE, suit),
                findCard(RankType.EIGHT, SuitType.SPADES),
                findCard(RankType.TEN, SuitType.DIAMONDS)
        );
    }

    public static List<Card> createUpperStraightFlush() {
        SuitType suit = SuitType.CLUBS;
        return List.of(
                findCard(RankType.NINE, suit),
                findCard(RankType.TEN, suit),
                findCard(RankType.JACK, suit),
                findCard(RankType.QUEEN, suit),
                findCard(RankType.KING, suit),
                findCard(RankType.DEUCE, SuitType.DIAMONDS),
                findCard(RankType.TREY, SuitType.SPADES)
        );
    }

    public static List<Card> createStraightFlush() {
        SuitType suit = SuitType.CLUBS;
        return List.of(
                findCard(RankType.FIVE, suit),
                findCard(RankType.SIX, suit),
                findCard(RankType.SEVEN, suit),
                findCard(RankType.EIGHT, suit),
                findCard(RankType.NINE, suit),
                findCard(RankType.DEUCE, SuitType.DIAMONDS),
                findCard(RankType.TREY, SuitType.SPADES)
        );
    }

    public static List<Card> createLowerStraightFlush() {
        SuitType suit = SuitType.CLUBS;
        return List.of(
                findCard(RankType.ACE, suit),
                findCard(RankType.DEUCE, suit),
                findCard(RankType.TREY, suit),
                findCard(RankType.FOUR, suit),
                findCard(RankType.FIVE, suit),
                findCard(RankType.JACK, SuitType.DIAMONDS),
                findCard(RankType.TEN, SuitType.SPADES)
        );
    }

    public static List<Card> createBothStraightAndFlush() {
        SuitType suit = SuitType.CLUBS;
        return List.of(
                findCard(RankType.ACE, suit),
                findCard(RankType.DEUCE, suit),
                findCard(RankType.TREY, suit),
                findCard(RankType.TEN, suit),
                findCard(RankType.QUEEN, suit),
                findCard(RankType.FOUR, SuitType.DIAMONDS),
                findCard(RankType.FIVE, SuitType.SPADES)
        );
    }

    public static List<Card> createFourOfAKind() {
        return List.of(
                findCard(RankType.NINE, SuitType.HEARTS),
                findCard(RankType.NINE, SuitType.CLUBS),
                findCard(RankType.NINE, SuitType.DIAMONDS),
                findCard(RankType.NINE, SuitType.SPADES),
                findCard(RankType.KING, SuitType.HEARTS),
                findCard(RankType.DEUCE, SuitType.DIAMONDS),
                findCard(RankType.TREY, SuitType.SPADES)
        );
    }

    public static List<Card> createFullHouse() {
        return List.of(
                findCard(RankType.TEN, SuitType.HEARTS),
                findCard(RankType.TEN, SuitType.CLUBS),
                findCard(RankType.TEN, SuitType.DIAMONDS),
                findCard(RankType.KING, SuitType.SPADES),
                findCard(RankType.KING, SuitType.HEARTS),
                findCard(RankType.DEUCE, SuitType.DIAMONDS),
                findCard(RankType.TREY, SuitType.SPADES)
        );
    }

    public static List<Card> createFlush() {
        return List.of(
                findCard(RankType.DEUCE, SuitType.HEARTS),
                findCard(RankType.FOUR, SuitType.HEARTS),
                findCard(RankType.SIX, SuitType.HEARTS),
                findCard(RankType.EIGHT, SuitType.HEARTS),
                findCard(RankType.KING, SuitType.HEARTS),
                findCard(RankType.ACE, SuitType.SPADES),
                findCard(RankType.JACK, SuitType.CLUBS)
        );
    }

    public static List<Card> createUpperStraight() {
        return List.of(
                findCard(RankType.TEN, SuitType.HEARTS),
                findCard(RankType.JACK, SuitType.CLUBS),
                findCard(RankType.QUEEN, SuitType.DIAMONDS),
                findCard(RankType.KING, SuitType.SPADES),
                findCard(RankType.ACE, SuitType.HEARTS),
                findCard(RankType.DEUCE, SuitType.DIAMONDS),
                findCard(RankType.TREY, SuitType.SPADES)
        );
    }

    public static List<Card> createStraight() {
        return List.of(
                findCard(RankType.FIVE, SuitType.HEARTS),
                findCard(RankType.SIX, SuitType.CLUBS),
                findCard(RankType.SEVEN, SuitType.DIAMONDS),
                findCard(RankType.EIGHT, SuitType.SPADES),
                findCard(RankType.NINE, SuitType.HEARTS),
                findCard(RankType.DEUCE, SuitType.DIAMONDS),
                findCard(RankType.TREY, SuitType.SPADES)
        );
    }

    public static List<Card> createLowerStraight() {
        return List.of(
                findCard(RankType.ACE, SuitType.HEARTS),
                findCard(RankType.DEUCE, SuitType.CLUBS),
                findCard(RankType.TREY, SuitType.DIAMONDS),
                findCard(RankType.FOUR, SuitType.SPADES),
                findCard(RankType.FIVE, SuitType.HEARTS),
                findCard(RankType.SEVEN, SuitType.DIAMONDS),
                findCard(RankType.NINE, SuitType.SPADES)
        );
    }


    public static List<Card> createThreeOfAKind() {
        return List.of(
                findCard(RankType.SEVEN, SuitType.HEARTS),
                findCard(RankType.SEVEN, SuitType.CLUBS),
                findCard(RankType.SEVEN, SuitType.DIAMONDS),
                findCard(RankType.KING, SuitType.SPADES),
                findCard(RankType.DEUCE, SuitType.HEARTS),
                findCard(RankType.TREY, SuitType.DIAMONDS),
                findCard(RankType.FOUR, SuitType.SPADES)
        );
    }

    public static List<Card> createTwoPair() {
        return List.of(
                findCard(RankType.FIVE, SuitType.HEARTS),
                findCard(RankType.FIVE, SuitType.CLUBS),
                findCard(RankType.KING, SuitType.DIAMONDS),
                findCard(RankType.KING, SuitType.SPADES),
                findCard(RankType.DEUCE, SuitType.HEARTS),
                findCard(RankType.TREY, SuitType.DIAMONDS),
                findCard(RankType.FOUR, SuitType.SPADES)
        );
    }

    public static List<Card> createOnePair() {
        return List.of(
                findCard(RankType.TEN, SuitType.HEARTS),
                findCard(RankType.TEN, SuitType.CLUBS),
                findCard(RankType.KING, SuitType.DIAMONDS),
                findCard(RankType.DEUCE, SuitType.SPADES),
                findCard(RankType.TREY, SuitType.HEARTS),
                findCard(RankType.FOUR, SuitType.DIAMONDS),
                findCard(RankType.FIVE, SuitType.SPADES)
        );
    }

    public static List<Card> createHighCard() {
        return List.of(
                findCard(RankType.DEUCE, SuitType.HEARTS),
                findCard(RankType.FOUR, SuitType.CLUBS),
                findCard(RankType.SEVEN, SuitType.DIAMONDS),
                findCard(RankType.NINE, SuitType.SPADES),
                findCard(RankType.JACK, SuitType.HEARTS),
                findCard(RankType.QUEEN, SuitType.DIAMONDS),
                findCard(RankType.KING, SuitType.SPADES)
        );
    }

    public static List<Card> createHandWithThreePairs() {
        return List.of(
                findCard(RankType.KING, SuitType.HEARTS),
                findCard(RankType.KING, SuitType.CLUBS),
                findCard(RankType.QUEEN, SuitType.DIAMONDS),
                findCard(RankType.QUEEN, SuitType.SPADES),
                findCard(RankType.JACK, SuitType.HEARTS),
                findCard(RankType.JACK, SuitType.DIAMONDS),
                findCard(RankType.DEUCE, SuitType.SPADES)
        );
    }

    public static List<Card> createFullHouseFromThreeOfAKindAndPair() {
        return List.of(
                findCard(RankType.SEVEN, SuitType.HEARTS),
                findCard(RankType.SEVEN, SuitType.CLUBS),
                findCard(RankType.SEVEN, SuitType.DIAMONDS),
                findCard(RankType.DEUCE, SuitType.SPADES),
                findCard(RankType.DEUCE, SuitType.HEARTS),
                findCard(RankType.KING, SuitType.DIAMONDS),
                findCard(RankType.QUEEN, SuitType.SPADES)
        );
    }

    public static List<Card> createStraightWithAPair() {
        return List.of(
                findCard(RankType.FIVE, SuitType.HEARTS),
                findCard(RankType.SIX, SuitType.CLUBS),
                findCard(RankType.SEVEN, SuitType.DIAMONDS),
                findCard(RankType.EIGHT, SuitType.SPADES),
                findCard(RankType.NINE, SuitType.HEARTS),
                findCard(RankType.NINE, SuitType.DIAMONDS),
                findCard(RankType.DEUCE, SuitType.SPADES)
        );
    }

    public static List<Card> createFlushWithLowerCards() {
        SuitType suit = SuitType.HEARTS;
        return List.of(
                findCard(RankType.DEUCE, suit),
                findCard(RankType.FOUR, suit),
                findCard(RankType.FIVE, suit),
                findCard(RankType.SIX, suit),
                findCard(RankType.SEVEN, suit),
                findCard(RankType.KING, SuitType.SPADES),
                findCard(RankType.QUEEN, SuitType.CLUBS)
        );
    }

    public static List<Card> createHighCardAceKicker() {
        return List.of(
                findCard(RankType.ACE, SuitType.HEARTS),
                findCard(RankType.KING, SuitType.CLUBS),
                findCard(RankType.QUEEN, SuitType.DIAMONDS),
                findCard(RankType.JACK, SuitType.SPADES),
                findCard(RankType.NINE, SuitType.HEARTS),
                findCard(RankType.DEUCE, SuitType.DIAMONDS),
                findCard(RankType.TREY, SuitType.SPADES)
        );
    }

    private static Card findCard(RankType rankType, SuitType suitType) {
        String key = rankType.toString() + suitType.toString();
        Card card = DECK_MAP.get(key);
        if (card == null) {
            throw new NotFoundException("Failed to find card: " + key + " - shouldn't get here, in a test");
        }
        return new Card(card);
    }
}
