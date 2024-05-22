package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.HandType;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
import com.twb.pokerapp.exception.NotFoundException;
import com.twb.pokerapp.service.game.DeckOfCardsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HandTypeEvaluatorTest {

    private HandTypeEvaluator evaluator;

    @BeforeEach
    public void beforeEach() {
        evaluator = new HandTypeEvaluator();
    }

    @Test
    public void testRoyalFlush() {
        List<Card> cards = createRoyalFlush();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.ROYAL_FLUSH, handType);
    }

    @Test
    public void testStraightFlush() {
        List<Card> cards = createStraightFlush();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.STRAIGHT_FLUSH, handType);
    }

    @Test
    public void testFourOfAKind() {
        List<Card> cards = createFourOfAKind();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.FOUR_OF_A_KIND, handType);
    }

    @Test
    public void testFullHouse() {
        List<Card> cards = createFullHouse();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.FULL_HOUSE, handType);
    }

    @Test
    public void testFlush() {
        List<Card> cards = createFlush();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.FLUSH, handType);
    }

    @Test
    public void testLowerStraight() {
        List<Card> cards = createLowerStraight();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.STRAIGHT, handType);
    }

    @Test
    public void testStraight() {
        List<Card> cards = createStraight();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.STRAIGHT, handType);
    }

    @Test
    public void testUpperStraight() {
        List<Card> cards = createUpperStraight();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.STRAIGHT, handType);
    }

    @Test
    public void testThreeOfAKind() {
        List<Card> cards = createThreeOfAKind();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.THREE_OF_A_KIND, handType);
    }

    @Test
    public void testTwoPair() {
        List<Card> cards = createTwoPair();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.TWO_PAIR, handType);
    }

    @Test
    public void testOnePair() {
        List<Card> cards = createOnePair();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.PAIR, handType);
    }

    @Test
    public void testHighCard() {
        List<Card> cards = createHighCard();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HandType.HIGH_CARD, handType);
    }

    private List<Card> createRoyalFlush() {
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

    private List<Card> createStraightFlush() {
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

    private List<Card> createFourOfAKind() {
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

    private List<Card> createFullHouse() {
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

    private List<Card> createFlush() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.DEUCE, SuitType.HEARTS));
        cardList.add(findCard(RankType.FOUR, SuitType.HEARTS));
        cardList.add(findCard(RankType.SIX, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.HEARTS));
        cardList.add(findCard(RankType.KING, SuitType.HEARTS));
        return cardList;
    }

    private List<Card> createLowerStraight() {
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

    private List<Card> createStraight() {
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

    private List<Card> createUpperStraight() {
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

    private List<Card> createThreeOfAKind() {
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

    private List<Card> createTwoPair() {
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

    private List<Card> createOnePair() {
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

    private List<Card> createHighCard() {
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

    private Card findCard(RankType rankType, SuitType suitType) {
        List<Card> cards = DeckOfCardsFactory.getCards(false);
        for (Card card : cards) {
            if (card.getRankType() == rankType && card.getSuitType() == suitType) {
                return new Card(card);
            }
        }
        throw new NotFoundException("Failed to find card - shouldn't get here, in a test");
    }
}