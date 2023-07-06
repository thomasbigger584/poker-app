package com.twb.pokergame.eval;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.Hand;

public interface HandEvaluator {
    int getRank(Hand hand);

    int getRank(Card[] cards);
}
