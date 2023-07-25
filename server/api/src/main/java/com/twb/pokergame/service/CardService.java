package com.twb.pokergame.service;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.Hand;
import com.twb.pokergame.domain.Round;
import com.twb.pokergame.domain.enumeration.CardType;
import com.twb.pokergame.domain.enumeration.SuitType;
import com.twb.pokergame.mapper.CardMapper;
import com.twb.pokergame.old.CardDTO;
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

    public Card createPlayerCard(Hand hand, CardDTO cardDto, CardType cardType) {
        Card card = mapCard(cardDto, cardType);
        card.setHand(hand);

        return repository.saveAndFlush(card);
    }

    public Card createCommunityCard(Round round, CardDTO cardDto, CardType cardType) {
        Card card = mapCard(cardDto, cardType);
        card.setRound(round);

        return repository.saveAndFlush(card);
    }

    private Card mapCard(CardDTO cardDto, CardType cardType) {
        Card card = new Card();
        card.setRank(cardDto.getRank());
        card.setRankValue(cardDto.getRankValue());
        card.setSuitType(getSuitType(cardDto.getSuit()));
        card.setCardType(cardType);
        return card;
    }

    private SuitType getSuitType(int suit) {
        return switch (suit) {
            case CardDTO.CLUBS -> SuitType.CLUBS;
            case CardDTO.DIAMONDS -> SuitType.DIAMONDS;
            case CardDTO.HEARTS -> SuitType.HEARTS;
            case CardDTO.SPADES -> SuitType.SPADES;
            default -> throw new IllegalArgumentException("Not a valid suit: " + suit);
        };
    }
}
