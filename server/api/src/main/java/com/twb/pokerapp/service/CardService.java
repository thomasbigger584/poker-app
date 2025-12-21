package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.mapper.CardMapper;
import com.twb.pokerapp.repository.CardRepository;
import com.twb.pokerapp.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class CardService {
    private final RoundRepository roundRepository;
    private final CardRepository repository;
    private final CardMapper mapper;

    public Card createPlayerCard(Hand hand, Card card) {
        card.setHand(hand);
        card = repository.save(card);
        return card;
    }

    public Card createCommunityCard(PokerTable table, Card card) {
        var roundOpt = roundRepository.findCurrentByTableId(table.getId());
        if (roundOpt.isEmpty()) {
            throw new IllegalStateException("Round not found");
        }
        card.setRound(roundOpt.get());
        card = repository.save(card);
        return card;
    }
}
