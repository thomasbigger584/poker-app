package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HandEvaluator {
    private final RankEvaluator rankEvaluator;
    private final HandTypeEvaluator handTypeEvaluator;

    public void evaluate(List<EvalPlayerHandDTO> playerHandList) {
        evaluateRankAndHandType(playerHandList);
        setWinners(playerHandList);
    }

    private void evaluateRankAndHandType(List<EvalPlayerHandDTO> playerHandList) {
        for (EvalPlayerHandDTO playerHand : playerHandList) {
            playerHand.setRank(rankEvaluator.getRank(playerHand.getCards()));
            playerHand.setHandType(handTypeEvaluator.evaluate(playerHand.getCards()));
        }
    }

    private void setWinners(List<EvalPlayerHandDTO> playerHandList) {
        playerHandList.sort(Comparator.reverseOrder());

        EvalPlayerHandDTO winningPlayer = playerHandList.getFirst();
        int winningRankValue = winningPlayer.getRank();
        for (EvalPlayerHandDTO playerHand : playerHandList) {
            if (playerHand.getRank() == winningRankValue) {
                playerHand.setWinner(true);
            }
        }
    }
}
