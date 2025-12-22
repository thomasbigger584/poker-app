package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.mapper.HandMapper;
import com.twb.pokerapp.repository.HandRepository;
import com.twb.pokerapp.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class HandService {
    private final RoundRepository roundRepository;
    private final HandRepository repository;
    private final HandMapper mapper;

    private final CardService cardService;

    public void addPlayerCard(PokerTable table, PlayerSession playerSession, Card card) {
        var roundOpt = roundRepository.findCurrentByTableId(table.getId());
        if (roundOpt.isEmpty()) {
            throw new IllegalStateException("Round not found");
        }
        var round = roundOpt.get();
        var handOpt = repository.findHandForRound(playerSession.getId(), round.getId());
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
