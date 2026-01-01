package com.twb.pokerapp.service.game.eval;

import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.repository.HandRepository;
import com.twb.pokerapp.service.game.eval.dto.EvalPlayerHandDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HandEvaluator {
    private final RankEvaluator rankEvaluator;
    private final HandTypeEvaluator handTypeEvaluator;
    private final HandRepository handRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void evaluate(Round round, List<EvalPlayerHandDTO> playerHandList) {
        evaluateRankAndHandType(playerHandList);
        savePlayerHandEvaluation(round, playerHandList);
    }

    private void evaluateRankAndHandType(List<EvalPlayerHandDTO> playerHandList) {
        for (var playerHand : playerHandList) {
            playerHand.setRank(rankEvaluator.getRank(playerHand.getCards()));
            playerHand.setHandType(handTypeEvaluator.evaluate(playerHand.getCards()));
        }
        playerHandList.sort(Comparator.reverseOrder());
    }

    private void savePlayerHandEvaluation(Round round, List<EvalPlayerHandDTO> playerHandsList) {
        var savingHands = new ArrayList<Hand>();
        for (var playerHand : playerHandsList) {
            var playerSession = playerHand.getPlayerSession();
            handRepository.findHandForRound(playerSession.getId(), round.getId())
                    .ifPresent(hand -> {
                        hand.setHandType(playerHand.getHandType());
                        savingHands.add(hand);
                    });
        }
        handRepository.saveAll(savingHands);
    }
}
