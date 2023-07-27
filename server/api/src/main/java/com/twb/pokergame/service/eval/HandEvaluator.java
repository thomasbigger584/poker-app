package com.twb.pokergame.service.eval;

import com.twb.pokergame.domain.enumeration.HandType;
import com.twb.pokergame.service.eval.dto.PlayerHandDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HandEvaluator {
    private final RankEvaluator rankEvaluator;
    private final HandTypeEvaluator handTypeEvaluator;

    public void evaluate(List<PlayerHandDTO> playerHandList) {
        //todo: remove folded players ... (?)

        evaluateRankAndHandType(playerHandList);
        setWinners(playerHandList);
    }

    private void evaluateRankAndHandType(List<PlayerHandDTO> playerHandList) {
        for (PlayerHandDTO playerHand : playerHandList) {
            int rank = rankEvaluator.getRank(playerHand.getCards());
            playerHand.setRank(rank);

            HandType handType = handTypeEvaluator.evaluate(playerHand.getCards());
            playerHand.setHandType(handType);
        }
    }

    private void setWinners(List<PlayerHandDTO> playerHandList) {
        playerHandList.sort(Comparator.reverseOrder());

        PlayerHandDTO winningPlayer = playerHandList.get(0);
        int winningRankValue = winningPlayer.getRank();
        for (PlayerHandDTO playerHand : playerHandList) {
            if (playerHand.getRank() == winningRankValue) {
                playerHand.setWinner(true);
            }
        }
    }
}
