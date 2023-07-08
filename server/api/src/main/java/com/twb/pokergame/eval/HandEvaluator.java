package com.twb.pokergame.eval;

import com.twb.pokergame.old.Card;
import com.twb.pokergame.old.Hand;

public interface HandEvaluator {
    int getRank(Hand hand);

    int getRank(Card[] cards);
}
