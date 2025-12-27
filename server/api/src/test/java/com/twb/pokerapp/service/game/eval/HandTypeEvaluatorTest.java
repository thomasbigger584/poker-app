package com.twb.pokerapp.service.game.eval;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.HandType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.twb.pokerapp.testutils.fixture.HandFixture.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HandTypeEvaluatorTest {

    private HandTypeEvaluator evaluator;

    static Stream<Arguments> cardHandTypeProvider() {
        return Stream.of(
                Arguments.of("Royal Flush", createRoyalFlush(), HandType.ROYAL_FLUSH),
                Arguments.of("Upper Straight Flush", createUpperStraightFlush(), HandType.STRAIGHT_FLUSH),
                Arguments.of("Middle Straight Flush", createStraightFlush(), HandType.STRAIGHT_FLUSH),
                Arguments.of("Lower Straight Flush", createLowerStraightFlush(), HandType.STRAIGHT_FLUSH),
                Arguments.of("Four of a Kind", createFourOfAKind(), HandType.FOUR_OF_A_KIND),
                Arguments.of("Full House", createFullHouse(), HandType.FULL_HOUSE),
                Arguments.of("Flush", createFlush(), HandType.FLUSH),
                Arguments.of("Straight & Flush", createBothStraightAndFlush(), HandType.FLUSH),
                Arguments.of("Upper Straight", createUpperStraight(), HandType.STRAIGHT),
                Arguments.of("Middle Straight", createStraight(), HandType.STRAIGHT),
                Arguments.of("Lower Straight", createLowerStraight(), HandType.STRAIGHT),
                Arguments.of("Three of a Kind", createThreeOfAKind(), HandType.THREE_OF_A_KIND),
                Arguments.of("Two Pair", createTwoPair(), HandType.TWO_PAIR),
                Arguments.of("Pair", createOnePair(), HandType.PAIR),
                Arguments.of("High Card", createHighCard(), HandType.HIGH_CARD),
                Arguments.of("Empty Hand", Collections.emptyList(), HandType.EMPTY_HAND),
                Arguments.of("Three Pairs", createHandWithThreePairs(), HandType.TWO_PAIR),
                Arguments.of("Three of a Kind and Pair", createFullHouseFromThreeOfAKindAndPair(), HandType.FULL_HOUSE),
                Arguments.of("Straight with Pair", createStraightWithAPair(), HandType.STRAIGHT),
                Arguments.of("Flush with lower cards", createFlushWithLowerCards(), HandType.FLUSH),
                Arguments.of("High card with Ace kicker", createHighCardAceKicker(), HandType.HIGH_CARD),
                Arguments.of("2 Three of a Kind", create2ThreeOfAKing(), HandType.FULL_HOUSE)
        );
    }

    @BeforeEach
    public void beforeEach() {
        evaluator = new HandTypeEvaluator();
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("cardHandTypeProvider")
    public void testHand(String scenario, List<Card> cards, HandType handType) {
        assertEquals(handType, evaluator.evaluate(cards));
    }
}