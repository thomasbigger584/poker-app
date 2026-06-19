package com.twb.pokerapp.service.game.eval;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.proto.HandType;
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
                Arguments.of("Royal Flush", createRoyalFlush(), HandType.HAND_TYPE_ROYAL_FLUSH),
                Arguments.of("Upper Straight Flush", createUpperStraightFlush(), HandType.HAND_TYPE_STRAIGHT_FLUSH),
                Arguments.of("Middle Straight Flush", createStraightFlush(), HandType.HAND_TYPE_STRAIGHT_FLUSH),
                Arguments.of("Lower Straight Flush", createLowerStraightFlush(), HandType.HAND_TYPE_STRAIGHT_FLUSH),
                Arguments.of("Four of a Kind", createFourOfAKind(), HandType.HAND_TYPE_FOUR_OF_A_KIND),
                Arguments.of("Full House", createFullHouse(), HandType.HAND_TYPE_FULL_HOUSE),
                Arguments.of("Flush", createFlush(), HandType.HAND_TYPE_FLUSH),
                Arguments.of("Straight & Flush", createBothStraightAndFlush(), HandType.HAND_TYPE_FLUSH),
                Arguments.of("Upper Straight", createUpperStraight(), HandType.HAND_TYPE_STRAIGHT),
                Arguments.of("Middle Straight", createStraight(), HandType.HAND_TYPE_STRAIGHT),
                Arguments.of("Lower Straight", createLowerStraight(), HandType.HAND_TYPE_STRAIGHT),
                Arguments.of("Three of a Kind", createThreeOfAKind(), HandType.HAND_TYPE_THREE_OF_A_KIND),
                Arguments.of("Two Pair", createTwoPair(), HandType.HAND_TYPE_TWO_PAIR),
                Arguments.of("Pair", createOnePair(), HandType.HAND_TYPE_PAIR),
                Arguments.of("High Card", createHighCard(), HandType.HAND_TYPE_HIGH_CARD),
                Arguments.of("Empty Hand", Collections.emptyList(), HandType.HAND_TYPE_EMPTY_HAND),
                Arguments.of("Three Pairs", createHandWithThreePairs(), HandType.HAND_TYPE_TWO_PAIR),
                Arguments.of("Three of a Kind and Pair", createFullHouseFromThreeOfAKindAndPair(), HandType.HAND_TYPE_FULL_HOUSE),
                Arguments.of("Straight with Pair", createStraightWithAPair(), HandType.HAND_TYPE_STRAIGHT),
                Arguments.of("Flush with lower cards", createFlushWithLowerCards(), HandType.HAND_TYPE_FLUSH),
                Arguments.of("High card with Ace kicker", createHighCardAceKicker(), HandType.HAND_TYPE_HIGH_CARD),
                Arguments.of("2 Three of a Kind", create2ThreeOfAKing(), HandType.HAND_TYPE_FULL_HOUSE)
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