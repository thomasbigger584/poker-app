package com.twb.pokergame.web.rest;


import com.twb.pokergame.eval.HandEvaluator;
import com.twb.pokergame.old.CardDTO;
import com.twb.pokergame.old.DeckOfCardsFactory;
import com.twb.pokergame.old.Hand;
import com.twb.pokergame.web.rest.dto.CardRequest;
import com.twb.pokergame.web.rest.dto.HandRankRequest;
import com.twb.pokergame.web.rest.dto.HandRankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GameResource {
    private final HandEvaluator evaluator;

    // this is for testing and will change, used to test end to end of hand evaluation
    @PostMapping("/rank")
    public HandRankResponse getHandRank(@RequestBody HandRankRequest request) {
        List<CardDTO> cards = new ArrayList<>();

        for (CardRequest cardRequest : request.getCardList()) {
            CardDTO card = DeckOfCardsFactory.findCard(cardRequest.getRank(), cardRequest.getSuit());
            cards.add(card);
        }

        Hand hand = new Hand(cards);
        int rank = evaluator.getRank(hand);
        return new HandRankResponse(rank);
    }

}
