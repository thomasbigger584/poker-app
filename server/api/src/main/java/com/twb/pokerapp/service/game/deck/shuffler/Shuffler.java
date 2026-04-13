package com.twb.pokerapp.service.game.deck.shuffler;

import com.twb.pokerapp.domain.Card;

import java.util.ArrayList;
import java.util.List;

public abstract class Shuffler {

    public List<Card> shuffle(List<Card> cards) {
        return onShuffle(new ArrayList<>(cards));
    }

    protected abstract List<Card> onShuffle(List<Card> cards);
}
