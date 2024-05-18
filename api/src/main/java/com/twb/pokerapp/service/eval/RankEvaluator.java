package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.domain.Card;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Evaluates the rank of a poker hand using a native method.
 */
@Component
public class RankEvaluator {

    static {
        // Load the native library containing the evaluator function
        System.load(System.getenv("EVALUATOR_SO_PATH"));
    }

    /**
     * Native method to evaluate the rank of a hand of cards.
     *
     * @param i the rank value of the first card
     * @param j the rank value of the second card
     * @param k the rank value of the third card
     * @param m the rank value of the fourth card
     * @param n the rank value of the fifth card
     * @param p the rank value of the sixth card
     * @param q the rank value of the seventh card
     * @return the rank of the hand
     */
    static native int getRank(int i, int j, int k, int m, int n, int p, int q);

    /**
     * Evaluates the rank of a hand of seven cards.
     *
     * @param cards the list of cards in the hand
     * @return the rank of the hand
     * @throws IllegalArgumentException if the number of cards is not seven
     */
    public int getRank(List<Card> cards) {
        if (cards.size() != 7) {
            throw new IllegalArgumentException("Not enough cards in hand: " + cards.size());
        }
        return getRank(cards.get(0).getRankValue(),
                cards.get(1).getRankValue(),
                cards.get(2).getRankValue(),
                cards.get(3).getRankValue(),
                cards.get(4).getRankValue(),
                cards.get(5).getRankValue(),
                cards.get(6).getRankValue());
    }
}
