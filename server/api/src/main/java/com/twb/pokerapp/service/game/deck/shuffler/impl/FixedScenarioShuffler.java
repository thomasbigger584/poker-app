package com.twb.pokerapp.service.game.deck.shuffler.impl;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.FixedScenario;
import com.twb.pokerapp.domain.enumeration.RankType;
import com.twb.pokerapp.domain.enumeration.SuitType;
import com.twb.pokerapp.repository.FixedScenarioRepository;
import com.twb.pokerapp.service.game.deck.shuffler.Shuffler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.use-fixed-scenario", havingValue = "true")
public class FixedScenarioShuffler extends Shuffler {
    private final FixedScenarioRepository fixedScenarioRepository;

    @Override
    protected List<Card> onShuffle(List<Card> cards) {
        Collections.shuffle(cards);
        var fixedScenarioOpt = fixedScenarioRepository.findTopBy();
        if (fixedScenarioOpt.isEmpty()) {
            return cards;
        }
        var fixedScenario = fixedScenarioOpt.get();
        var playerCards = fixedScenario.getPlayerHands()
                .stream().flatMap(cardStr -> parse(cards, cardStr)).toList();
        var communityCards = parseCommunityCards(cards, fixedScenario);
        var fixedCards = new ArrayList<Card>();
        for (var index = 1; index <= playerCards.size(); index++) {
            if (!isEven(index)) {
                fixedCards.add(playerCards.get(index));
            }
        }
        for (var index = 1; index <= playerCards.size(); index++) {
            if (isEven(index)) {
                fixedCards.add(playerCards.get(index));
            }
        }
        fixedCards.addAll(communityCards);
        fixedCards.addAll(cards.stream().filter(card -> {
            for (var fixedCard : fixedCards) {
                if (rankAndSuitCardEquals(fixedCard, card.getRankType(), card.getSuitType())) {
                    return true;
                }
            }
            return false;
        }).toList());
        return fixedCards;
    }

    private List<Card> parseCommunityCards(List<Card> cards, FixedScenario fixedScenario) {
        var fixedCommunityCards = fixedScenario.getCommunityCards();
        if (fixedCommunityCards == null) {
            return Collections.emptyList();
        }
        return parse(cards, fixedScenario.getCommunityCards()).toList();
    }

    private Stream<Card> parse(List<Card> cards, String cardsStr) {
        var cardsSplit = cardsStr.split(";");
        var cardsFound = new ArrayList<Card>();
        for (var cardStr : cardsSplit) {
            var rankType = RankType.fromRankChar(cardStr.charAt(0));
            var suitType = SuitType.fromSuitChar(cardStr.charAt(1));
            var foundCard = cards.stream()
                    .filter(card -> rankAndSuitCardEquals(card, rankType, suitType))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid card: " + cardStr));
            cardsFound.add(foundCard);
        }
        return cardsFound.stream();
    }

    private boolean rankAndSuitCardEquals(Card card, RankType rankType, SuitType suitType) {
        return card.getRankType().equals(rankType) && card.getSuitType().equals(suitType);
    }

    private boolean isEven(int index) {
        return index % 2 == 0;
    }
}
