package com.twb.pokerapp.testutils.fixture;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.proto.RankType;
import com.twb.pokerapp.proto.SuitType;
import com.twb.pokerapp.service.game.deck.DeckFactory;
import com.twb.pokerapp.web.exception.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HandFixture {

    private static final Map<String, Card> DECK_MAP = DeckFactory.CARDS.stream()
            .collect(Collectors.toUnmodifiableMap(card -> card.getRankType().toString() + card.getSuitType().toString(), Function.identity()));

    public static List<Card> createRoyalFlush() {
        var suit = SuitType.SUIT_TYPE_HEARTS;
        return List.of(
                findCard(RankType.RANK_TYPE_TEN, suit),
                findCard(RankType.RANK_TYPE_JACK, suit),
                findCard(RankType.RANK_TYPE_QUEEN, suit),
                findCard(RankType.RANK_TYPE_KING, suit),
                findCard(RankType.RANK_TYPE_ACE, suit),
                findCard(RankType.RANK_TYPE_EIGHT, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_TEN, SuitType.SUIT_TYPE_DIAMONDS)
        );
    }

    public static List<Card> createUpperStraightFlush() {
        var suit = SuitType.SUIT_TYPE_CLUBS;
        return List.of(
                findCard(RankType.RANK_TYPE_NINE, suit),
                findCard(RankType.RANK_TYPE_TEN, suit),
                findCard(RankType.RANK_TYPE_JACK, suit),
                findCard(RankType.RANK_TYPE_QUEEN, suit),
                findCard(RankType.RANK_TYPE_KING, suit),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createStraightFlush() {
        var suit = SuitType.SUIT_TYPE_CLUBS;
        return List.of(
                findCard(RankType.RANK_TYPE_FIVE, suit),
                findCard(RankType.RANK_TYPE_SIX, suit),
                findCard(RankType.RANK_TYPE_SEVEN, suit),
                findCard(RankType.RANK_TYPE_EIGHT, suit),
                findCard(RankType.RANK_TYPE_NINE, suit),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createLowerStraightFlush() {
        var suit = SuitType.SUIT_TYPE_CLUBS;
        return List.of(
                findCard(RankType.RANK_TYPE_ACE, suit),
                findCard(RankType.RANK_TYPE_DEUCE, suit),
                findCard(RankType.RANK_TYPE_TREY, suit),
                findCard(RankType.RANK_TYPE_FOUR, suit),
                findCard(RankType.RANK_TYPE_FIVE, suit),
                findCard(RankType.RANK_TYPE_JACK, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_TEN, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createBothStraightAndFlush() {
        var suit = SuitType.SUIT_TYPE_CLUBS;
        return List.of(
                findCard(RankType.RANK_TYPE_ACE, suit),
                findCard(RankType.RANK_TYPE_DEUCE, suit),
                findCard(RankType.RANK_TYPE_TREY, suit),
                findCard(RankType.RANK_TYPE_TEN, suit),
                findCard(RankType.RANK_TYPE_QUEEN, suit),
                findCard(RankType.RANK_TYPE_FOUR, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_FIVE, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createFourOfAKind() {
        return List.of(
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createFullHouse() {
        return List.of(
                findCard(RankType.RANK_TYPE_TEN, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_TEN, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_TEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createFlush() {
        return List.of(
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_FOUR, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_SIX, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_EIGHT, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_ACE, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_JACK, SuitType.SUIT_TYPE_CLUBS)
        );
    }

    public static List<Card> createUpperStraight() {
        return List.of(
                findCard(RankType.RANK_TYPE_TEN, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_JACK, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_QUEEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_ACE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createStraight() {
        return List.of(
                findCard(RankType.RANK_TYPE_FIVE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_SIX, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_EIGHT, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createLowerStraight() {
        return List.of(
                findCard(RankType.RANK_TYPE_ACE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_FOUR, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_FIVE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_SPADES)
        );
    }


    public static List<Card> createThreeOfAKind() {
        return List.of(
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_FOUR, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createTwoPair() {
        return List.of(
                findCard(RankType.RANK_TYPE_FIVE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_FIVE, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_FOUR, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createOnePair() {
        return List.of(
                findCard(RankType.RANK_TYPE_TEN, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_TEN, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_FOUR, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_FIVE, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createHighCard() {
        return List.of(
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_FOUR, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_JACK, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_QUEEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createHandWithThreePairs() {
        return List.of(
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_QUEEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_QUEEN, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_JACK, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_JACK, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createFullHouseFromThreeOfAKindAndPair() {
        return List.of(
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_QUEEN, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createStraightWithAPair() {
        return List.of(
                findCard(RankType.RANK_TYPE_FIVE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_SIX, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_EIGHT, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> createFlushWithLowerCards() {
        SuitType suit = SuitType.SUIT_TYPE_HEARTS;
        return List.of(
                findCard(RankType.RANK_TYPE_DEUCE, suit),
                findCard(RankType.RANK_TYPE_FOUR, suit),
                findCard(RankType.RANK_TYPE_FIVE, suit),
                findCard(RankType.RANK_TYPE_SIX, suit),
                findCard(RankType.RANK_TYPE_SEVEN, suit),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_QUEEN, SuitType.SUIT_TYPE_CLUBS)
        );
    }

    public static List<Card> createHighCardAceKicker() {
        return List.of(
                findCard(RankType.RANK_TYPE_ACE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_KING, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_QUEEN, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_JACK, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_NINE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_TREY, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static List<Card> create2ThreeOfAKing() {
        return List.of(
                findCard(RankType.RANK_TYPE_ACE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_ACE, SuitType.SUIT_TYPE_CLUBS),
                findCard(RankType.RANK_TYPE_ACE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_SPADES),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_HEARTS),
                findCard(RankType.RANK_TYPE_DEUCE, SuitType.SUIT_TYPE_DIAMONDS),
                findCard(RankType.RANK_TYPE_SEVEN, SuitType.SUIT_TYPE_SPADES)
        );
    }

    public static Card findCard(RankType rankType, SuitType suitType) {
        var key = rankType.toString() + suitType.toString();
        var card = DECK_MAP.get(key);
        if (card == null) {
            throw new NotFoundException("Failed to find card: " + key + " - shouldn't get here, in a test");
        }
        return new Card(card);
    }
}
