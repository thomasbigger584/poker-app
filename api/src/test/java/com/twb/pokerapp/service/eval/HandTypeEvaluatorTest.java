package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.HandType;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
import com.twb.pokerapp.exception.NotFoundException;
import com.twb.pokerapp.service.game.DeckOfCardsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandTypeEvaluatorTest {

    private HandTypeEvaluator evaluator;

    @BeforeEach
    public void beforeEach() {
        evaluator = new HandTypeEvaluator();
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("cardHandTypeProvider")
    public void testHand(String scenario, List<Card> cards, HandType handType) {
        assertEquals(handType, evaluator.evaluate(cards));
    }

    static Stream<Arguments> cardHandTypeProvider() {
        return Stream.of(
                Arguments.of("Royal Flush", createRoyalFlush(), HandType.ROYAL_FLUSH),
                Arguments.of("Straight Flush", createStraightFlush(), HandType.STRAIGHT_FLUSH),
                Arguments.of("Four of a Kind", createFourOfAKind(), HandType.FOUR_OF_A_KIND),
                Arguments.of("Full House", createFullHouse(), HandType.FULL_HOUSE),
                Arguments.of("Flush", createFlush(), HandType.FLUSH),
                Arguments.of("Lower Straight", createLowerStraight(), HandType.STRAIGHT),
                Arguments.of("Middle Straight", createStraight(), HandType.STRAIGHT),
                Arguments.of("Upper Straight", createUpperStraight(), HandType.STRAIGHT),
                Arguments.of("Three of a Kind", createThreeOfAKind(), HandType.THREE_OF_A_KIND),
                Arguments.of("Two Pair", createTwoPair(), HandType.TWO_PAIR),
                Arguments.of("Pair", createOnePair(), HandType.PAIR),
                Arguments.of("High Card", createHighCard(), HandType.HIGH_CARD),
                Arguments.of("Empty Hand", Collections.emptyList(), HandType.EMPTY_HAND)
        );
    }

    private static List<Card> createRoyalFlush() {
        List<Card> cardList = new ArrayList<>();
        SuitType suit = SuitType.HEARTS;
        cardList.add(findCard(RankType.TEN, suit));
        cardList.add(findCard(RankType.JACK, suit));
        cardList.add(findCard(RankType.QUEEN, suit));
        cardList.add(findCard(RankType.KING, suit));
        cardList.add(findCard(RankType.ACE, suit));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.TEN, SuitType.DIAMONDS));
        return cardList;
    }

    private static List<Card> createStraightFlush() {
        List<Card> cardList = new ArrayList<>();
        SuitType suit = SuitType.CLUBS;
        cardList.add(findCard(RankType.FIVE, suit));
        cardList.add(findCard(RankType.SIX, suit));
        cardList.add(findCard(RankType.SEVEN, suit));
        cardList.add(findCard(RankType.EIGHT, suit));
        cardList.add(findCard(RankType.NINE, suit));
        cardList.add(findCard(RankType.DEUCE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.TREY, SuitType.SPADES));
        return cardList;
    }

    private static List<Card> createFourOfAKind() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.NINE, SuitType.HEARTS));
        cardList.add(findCard(RankType.NINE, SuitType.CLUBS));
        cardList.add(findCard(RankType.NINE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.NINE, SuitType.SPADES));
        cardList.add(findCard(RankType.KING, SuitType.HEARTS));
        cardList.add(findCard(RankType.DEUCE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.TREY, SuitType.SPADES));
        return cardList;
    }

    private static List<Card> createFullHouse() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.TEN, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.KING, SuitType.SPADES));
        cardList.add(findCard(RankType.KING, SuitType.HEARTS));
        cardList.add(findCard(RankType.DEUCE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.TREY, SuitType.SPADES));
        return cardList;
    }

    private static List<Card> createFlush() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.DEUCE, SuitType.HEARTS));
        cardList.add(findCard(RankType.FOUR, SuitType.HEARTS));
        cardList.add(findCard(RankType.SIX, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.HEARTS));
        cardList.add(findCard(RankType.KING, SuitType.HEARTS));
        return cardList;
    }

    private static List<Card> createLowerStraight() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.ACE, SuitType.HEARTS));
        cardList.add(findCard(RankType.DEUCE, SuitType.CLUBS));
        cardList.add(findCard(RankType.TREY, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.FOUR, SuitType.SPADES));
        cardList.add(findCard(RankType.FIVE, SuitType.HEARTS));
        cardList.add(findCard(RankType.SEVEN, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.NINE, SuitType.SPADES));
        return cardList;
    }

    private static List<Card> createStraight() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.FIVE, SuitType.HEARTS));
        cardList.add(findCard(RankType.SIX, SuitType.CLUBS));
        cardList.add(findCard(RankType.SEVEN, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.NINE, SuitType.HEARTS));
        cardList.add(findCard(RankType.DEUCE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.TREY, SuitType.SPADES));
        return cardList;
    }

    private static List<Card> createUpperStraight() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.JACK, SuitType.CLUBS));
        cardList.add(findCard(RankType.QUEEN, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.KING, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.HEARTS));
        cardList.add(findCard(RankType.DEUCE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.TREY, SuitType.SPADES));
        return cardList;
    }

    private static List<Card> createThreeOfAKind() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.SEVEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.SEVEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.SEVEN, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.KING, SuitType.SPADES));
        cardList.add(findCard(RankType.DEUCE, SuitType.HEARTS));
        cardList.add(findCard(RankType.TREY, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.FOUR, SuitType.SPADES));
        return cardList;
    }

    private static List<Card> createTwoPair() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.FIVE, SuitType.HEARTS));
        cardList.add(findCard(RankType.FIVE, SuitType.CLUBS));
        cardList.add(findCard(RankType.KING, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.KING, SuitType.SPADES));
        cardList.add(findCard(RankType.DEUCE, SuitType.HEARTS));
        cardList.add(findCard(RankType.TREY, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.FOUR, SuitType.SPADES));
        return cardList;
    }

    private static List<Card> createOnePair() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.KING, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.DEUCE, SuitType.SPADES));
        cardList.add(findCard(RankType.TREY, SuitType.HEARTS));
        cardList.add(findCard(RankType.FOUR, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.FIVE, SuitType.SPADES));
        return cardList;
    }

    private static List<Card> createHighCard() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.DEUCE, SuitType.HEARTS));
        cardList.add(findCard(RankType.FOUR, SuitType.CLUBS));
        cardList.add(findCard(RankType.SEVEN, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.NINE, SuitType.SPADES));
        cardList.add(findCard(RankType.JACK, SuitType.HEARTS));
        cardList.add(findCard(RankType.QUEEN, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.KING, SuitType.SPADES));
        return cardList;
    }

    private static Card findCard(RankType rankType, SuitType suitType) {
        List<Card> cards = DeckOfCardsFactory.getCards(false);
        for (Card card : cards) {
            if (card.getRankType() == rankType && card.getSuitType() == suitType) {
                return new Card(card);
            }
        }
        throw new NotFoundException("Failed to find card - shouldn't get here, in a test");
    }
}