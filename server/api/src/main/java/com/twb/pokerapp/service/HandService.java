package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.mapper.HandMapper;
import com.twb.pokerapp.repository.HandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class HandService {
    private final HandRepository repository;
    private final HandMapper mapper;

    private final CardService cardService;

    public void addPlayerCard(PlayerSession playerSession, Round round, Card card) {
        var handOpt = repository.findHandForRound(playerSession.getId(), round.getId());
        Hand hand;
        if (handOpt.isPresent()) {
            hand = handOpt.get();
        } else {
            hand = new Hand();
            hand.setRound(round);
            hand.setPlayerSession(playerSession);
            hand = repository.saveAndFlush(hand);
        }
        cardService.createPlayerCard(hand, card);
    }
}
