package com.twb.pokergame.eval;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.Hand;

public class SevenCardHandEvaluator {
    static {
        System.load(System.getenv("EVALUATOR_SO_PATH"));
    }

    private SevenCardHandEvaluator() {
    }

    static native int getRank(int i, int j, int k, int m, int n, int p, int q);

    public static int getRank(Hand hand) {
        if (hand.size() != 7) {
            throw new IllegalArgumentException("Not enough cards in hand: " + hand.size());
        }
        return getRank(hand.get(0).getRankValue(),
                hand.get(1).getRankValue(),
                hand.get(2).getRankValue(),
                hand.get(3).getRankValue(),
                hand.get(4).getRankValue(),
                hand.get(5).getRankValue(),
                hand.get(6).getRankValue());
    }

    public static int getRank(Card[] cards) {
        if (cards.length != 7) {
            throw new IllegalArgumentException("Not enough cards in hand: " + cards.length);
        }
        return getRank(cards[0].getRankValue(),
                cards[1].getRankValue(),
                cards[2].getRankValue(),
                cards[3].getRankValue(),
                cards[4].getRankValue(),
                cards[5].getRankValue(),
                cards[6].getRankValue());
    }
}
