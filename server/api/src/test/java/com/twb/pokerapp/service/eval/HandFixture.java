package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
import com.twb.pokerapp.exception.NotFoundException;
import com.twb.pokerapp.service.game.DeckOfCardsFactory;

import java.util.ArrayList;
import java.util.List;

public class HandFixture {

    public static List<Card> createRoyalFlush() {
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

    public static List<Card> createUpperStraightFlush() {
        List<Card> cardList = new ArrayList<>();
        SuitType suit = SuitType.CLUBS;
        cardList.add(findCard(RankType.NINE, suit));
        cardList.add(findCard(RankType.TEN, suit));
        cardList.add(findCard(RankType.JACK, suit));
        cardList.add(findCard(RankType.QUEEN, suit));
        cardList.add(findCard(RankType.KING, suit));
        cardList.add(findCard(RankType.DEUCE, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.TREY, SuitType.SPADES));
        return cardList;
    }

    public static List<Card> createStraightFlush() {
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

    public static List<Card> createLowerStraightFlush() {
        List<Card> cardList = new ArrayList<>();
        SuitType suit = SuitType.CLUBS;
        cardList.add(findCard(RankType.ACE, suit));
        cardList.add(findCard(RankType.DEUCE, suit));
        cardList.add(findCard(RankType.TREY, suit));
        cardList.add(findCard(RankType.FOUR, suit));
        cardList.add(findCard(RankType.FIVE, suit));
        cardList.add(findCard(RankType.JACK, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.TEN, SuitType.SPADES));
        return cardList;
    }

    public static List<Card> createBothStraightAndFlush() {
        List<Card> cardList = new ArrayList<>();
        SuitType suit = SuitType.CLUBS;
        cardList.add(findCard(RankType.ACE, suit));
        cardList.add(findCard(RankType.DEUCE, suit));
        cardList.add(findCard(RankType.TREY, suit));
        cardList.add(findCard(RankType.TEN, suit));
        cardList.add(findCard(RankType.QUEEN, suit));
        cardList.add(findCard(RankType.FOUR, SuitType.DIAMONDS));
        cardList.add(findCard(RankType.FIVE, SuitType.SPADES));
        return cardList;
    }

    public static List<Card> createFourOfAKind() {
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

    public static List<Card> createFullHouse() {
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

    public static List<Card> createFlush() {
        List<Card> cardList = new ArrayList<>();
        cardList.add(findCard(RankType.DEUCE, SuitType.HEARTS));
        cardList.add(findCard(RankType.FOUR, SuitType.HEARTS));
        cardList.add(findCard(RankType.SIX, SuitType.HEARTS));
        cardList.add(findCard(RankType.EIGHT, SuitType.HEARTS));
        cardList.add(findCard(RankType.KING, SuitType.HEARTS));
        return cardList;
    }

    public static List<Card> createUpperStraight() {
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

    public static List<Card> createStraight() {
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

    public static List<Card> createLowerStraight() {
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


    public static List<Card> createThreeOfAKind() {
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

    public static List<Card> createTwoPair() {
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

    public static List<Card> createOnePair() {
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

    public static List<Card> createHighCard() {
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
