package com.twb.pokergame.rest;


import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.DeckOfCardsFactory;
import com.twb.pokergame.domain.Hand;
import com.twb.pokergame.rest.dto.CardRequest;
import com.twb.pokergame.rest.dto.HandRankRequest;
import com.twb.pokergame.rest.dto.HandRankResponse;
import com.twb.pokergame.eval.HandEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/poker-game")
@RequiredArgsConstructor
public class PokerGameResource {
    private final HandEvaluator evaluator;

    @PostMapping("/rank")
    public HandRankResponse getHandRank(@RequestBody HandRankRequest request) {
        List<Card> cards = new ArrayList<>();

        for (CardRequest cardRequest : request.getCardList()) {
            Card card = DeckOfCardsFactory.findCard(cardRequest.getRank(), cardRequest.getSuit());
            cards.add(card);
        }

        Hand hand = new Hand(cards);
        int rank = evaluator.getRank(hand);
        return new HandRankResponse(rank);
    }

}
