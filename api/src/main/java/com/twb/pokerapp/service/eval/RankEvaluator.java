package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.domain.Card;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RankEvaluator {
    static {
        System.load(System.getenv("EVALUATOR_SO_PATH"));
    }

    static native int getRank(int i, int j, int k, int m, int n, int p, int q);

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
