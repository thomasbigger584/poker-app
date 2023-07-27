package com.twb.pokergame.service;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.Hand;
import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.CardType;
import com.twb.pokergame.mapper.HandMapper;
import com.twb.pokergame.repository.HandRepository;
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
        Optional<Hand> handOpt =
                repository.findHandForRound(playerSession.getId(), round.getId());
        Hand hand;
        if (handOpt.isPresent()) {
            hand = handOpt.get();
        } else {
            hand = new Hand();
            hand.setRound(round);
            hand.setPlayerSession(playerSession);
            hand = repository.save(hand);
        }
        cardService.createPlayerCard(hand, card);
    }
}
