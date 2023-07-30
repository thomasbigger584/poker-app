package com.twb.pokergame.service.eval;

import com.twb.pokergame.domain.enumeration.HandType;
import com.twb.pokergame.service.eval.dto.EvalPlayerHandDTO;
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
        //todo: remove folded players ... (?)

        evaluateRankAndHandType(playerHandList);
        setWinners(playerHandList);
    }

    private void evaluateRankAndHandType(List<EvalPlayerHandDTO> playerHandList) {
        for (EvalPlayerHandDTO playerHand : playerHandList) {
            int rank = rankEvaluator.getRank(playerHand.getCards());
            playerHand.setRank(rank);

            HandType handType = handTypeEvaluator.evaluate(playerHand.getCards());
            playerHand.setHandType(handType);
        }
    }

    private void setWinners(List<EvalPlayerHandDTO> playerHandList) {
        playerHandList.sort(Comparator.reverseOrder());

        EvalPlayerHandDTO winningPlayer = playerHandList.get(0);
        int winningRankValue = winningPlayer.getRank();
        for (EvalPlayerHandDTO playerHand : playerHandList) {
            if (playerHand.getRank() == winningRankValue) {
                playerHand.setWinner(true);
            }
        }
    }
}
