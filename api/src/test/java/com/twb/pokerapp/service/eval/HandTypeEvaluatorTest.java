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

import static com.twb.pokerapp.domain.enumeration.HandType.*;
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
        assertEquals(ROYAL_FLUSH, handType);
    }

    @Test
    public void testStraightFlushMiddle() {
        List<Card> cards = createStraightFlushMiddle();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(STRAIGHT_FLUSH, handType);
    }

    @Test
    public void testStraightFlushAceLower() {
        List<Card> cards = createStraightFlushMiddle();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(STRAIGHT_FLUSH, handType);
    }

    @Test
    public void testStraightFlushAceHigher() {
        List<Card> cards = createStraightFlushMiddle();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(STRAIGHT_FLUSH, handType);
    }

    @Test
    public void testFourOfAKind() {
        List<Card> cards = createFourOfAKind();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(FOUR_OF_A_KIND, handType);
    }

    @Test
    public void testFullHouse() {
        List<Card> cards = createFullHouse();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(FULL_HOUSE, handType);
    }

    @Test
    public void testFlush() {
        List<Card> cards = createFlush();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(FLUSH, handType);
    }

    @Test
    public void testStraightMiddle() {
        List<Card> cards = createStraightMiddle();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(STRAIGHT, handType);
    }

    @Test
    public void testStraight() {
        List<Card> cards = createStraight();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(STRAIGHT, handType);
    }

    @Test
    public void testStraightWithDuplicates() {
        List<Card> cards = createStraightDuplicate();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(STRAIGHT, handType);
    }

    @Test
    public void testStraightAceLower() {
        List<Card> cards = createStraightAceLower();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(STRAIGHT, handType);
    }

    @Test
    public void testStraightAceHigher() {
        List<Card> cards = createStraightAceHigher();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(STRAIGHT, handType);
    }

    @Test
    public void testThreeOfAKind() {
        List<Card> cards = createThreeOfAKind();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(THREE_OF_A_KIND, handType);
    }

    @Test
    public void testTwoPair() {
        List<Card> cards = createTwoPair();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(TWO_PAIR, handType);
    }

    @Test
    public void testPair() {
        List<Card> cards = createPair();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(PAIR, handType);
    }

    @Test
    public void testHighCard() {
        List<Card> cards = createHighCard();
        HandType handType = evaluator.evaluate(cards);
        assertEquals(HIGH_CARD, handType);
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

    private List<Card> createStraightFlushMiddle() {
        List<Card> cardList = new ArrayList<>();
        SuitType suit = SuitType.CLUBS;
        cardList.add(findCard(RankType.SEVEN, suit));
        cardList.add(findCard(RankType.EIGHT, suit));
        cardList.add(findCard(RankType.NINE, suit));
        cardList.add(findCard(RankType.TEN, suit));
        cardList.add(findCard(RankType.JACK, suit));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.TEN, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createFourOfAKind() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.ACE, SuitType.CLUBS));
        cardList.add(findCard(RankType.FOUR, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.QUEEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.ACE, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createFullHouse() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.TEN, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.QUEEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.ACE, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createFlush() {
        List<Card> cardList = new ArrayList<>();
        SuitType suit = SuitType.CLUBS;
        cardList.add(findCard(RankType.TEN, suit));
        cardList.add(findCard(RankType.FOUR, suit));
        cardList.add(findCard(RankType.QUEEN, suit));
        cardList.add(findCard(RankType.SEVEN, suit));
        cardList.add(findCard(RankType.ACE, suit));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.TEN, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createStraightMiddle() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.FIVE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.SIX, SuitType.HEARTS));
        cardList.add(findCard(RankType.SEVEN, SuitType.SPADES));
        cardList.add(findCard(RankType.EIGHT, SuitType.HEARTS));
        cardList.add(findCard(RankType.NINE, SuitType.SPADES));
        cardList.add(findCard(RankType.JACK, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createStraight() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TREY, SuitType.HEARTS));
        cardList.add(findCard(RankType.FIVE, SuitType.CLUBS));
        cardList.add(findCard(RankType.SIX, SuitType.HEARTS));
        cardList.add(findCard(RankType.SEVEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.SEVEN, SuitType.SPADES));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.NINE, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createStraightDuplicate() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.FIVE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.SIX, SuitType.HEARTS));
        cardList.add(findCard(RankType.SEVEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.SIX, SuitType.SPADES));
        cardList.add(findCard(RankType.NINE, SuitType.DIAMONDS));
        return cardList;
    }


    private List<Card> createStraightAceLower() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.ACE, SuitType.CLUBS));
        cardList.add(findCard(RankType.DEUCE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.TREY, SuitType.HEARTS));
        cardList.add(findCard(RankType.FOUR, SuitType.SPADES));
        cardList.add(findCard(RankType.FIVE, SuitType.HEARTS));
        cardList.add(findCard(RankType.NINE, SuitType.SPADES));
        cardList.add(findCard(RankType.JACK, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createStraightAceHigher() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.JACK, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.QUEEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.KING, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.HEARTS));
        cardList.add(findCard(RankType.SEVEN, SuitType.SPADES));
        cardList.add(findCard(RankType.FIVE, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createThreeOfAKind() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.FOUR, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.QUEEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.ACE, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createTwoPair() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.FOUR, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.QUEEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.FOUR, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createPair() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.FOUR, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.QUEEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.SEVEN, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.DIAMONDS));
        return cardList;
    }

    private List<Card> createHighCard() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.TEN, SuitType.CLUBS));
        cardList.add(findCard(RankType.FOUR, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.QUEEN, SuitType.HEARTS));
        cardList.add(findCard(RankType.SEVEN, SuitType.SPADES));
        cardList.add(findCard(RankType.ACE, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.SPADES));
        cardList.add(findCard(RankType.JACK, SuitType.DIAMONDS));
        return cardList;
    }

    private Card findCard(RankType rankType, SuitType suitType) {
        List<Card> cards = DeckOfCardsFactory.getCards(false);
        for (Card card : cards) {
            if (card.getRankType() == rankType && card.getSuitType() == suitType) {
                return new Card(card);
            }
        }
        throw new NotFoundException("Failed to find card - shouldnt get here, in a test");
    }
}