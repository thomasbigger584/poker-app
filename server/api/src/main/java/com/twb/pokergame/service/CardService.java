package com.twb.pokergame.service;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.Hand;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.CardType;
import com.twb.pokergame.mapper.CardMapper;
import com.twb.pokergame.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class CardService {
    private final CardRepository repository;
    private final CardMapper mapper;

    public Card createPlayerCard(Hand hand, Card card) {
        Card newCard = new Card(card);
        newCard.setHand(hand);

        return repository.saveAndFlush(newCard);
    }

    public Card createCommunityCard(Round round, Card card) {
        Card newCard = new Card(card);
        newCard.setRound(round);

        return repository.saveAndFlush(newCard);
    }
}
