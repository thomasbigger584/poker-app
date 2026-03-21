package com.twb.pokerapp.service.game.deck.shuffler.impl;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.service.game.deck.shuffler.Shuffler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnMissingBean(Shuffler.class)
public class DefaultRandomShuffler extends Shuffler {
    @Override
    protected List<Card> onShuffle(List<Card> cards) {
        Collections.shuffle(cards);
        return cards;
    }
}
