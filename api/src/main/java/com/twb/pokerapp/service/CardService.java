package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.mapper.CardMapper;
import com.twb.pokerapp.repository.CardRepository;
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
        card.setHand(hand);
        return repository.saveAndFlush(card);
    }

    public Card createCommunityCard(Round round, Card card) {
        card.setRound(round);
        return repository.saveAndFlush(card);
    }
}
