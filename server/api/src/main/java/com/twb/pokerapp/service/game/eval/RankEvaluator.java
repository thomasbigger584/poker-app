package com.twb.pokerapp.service.game.eval;

import com.twb.pokerapp.domain.Card;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Evaluates the rank of a poker hand using a native method.
 */
@Slf4j
@Component
public class RankEvaluator {
    static {
        try {
            String path = System.getenv("EVALUATOR_SO_PATH");
            if (path != null) {
                System.load(path);
                log.info("Successfully loaded native library.");
            } else {
                log.error("EVALUATOR_SO_PATH environment variable is not set.");
            }
        } catch (UnsatisfiedLinkError e) {
            log.error("Failed to load native library", e);
        } catch (Throwable t) {
            log.error("Unexpected error loading native library", t);
        }
    }

    /**
     * Native method to evaluate the rank of a hand of cards.
     *
     * @param c1 the rank value of the first card
     * @param c2 the rank value of the second card
     * @param c3 the rank value of the third card
     * @param c4 the rank value of the fourth card
     * @param c5 the rank value of the fifth card
     * @param c6 the rank value of the sixth card
     * @param c7 the rank value of the seventh card
     * @return the rank of the hand
     */
    static native int getRankNative(int c1, int c2, int c3, int c4, int c5, int c6, int c7);

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
        return getRankNative(cards.get(0).getRankValue(),
                cards.get(1).getRankValue(),
                cards.get(2).getRankValue(),
                cards.get(3).getRankValue(),
                cards.get(4).getRankValue(),
                cards.get(5).getRankValue(),
                cards.get(6).getRankValue());
    }
}
