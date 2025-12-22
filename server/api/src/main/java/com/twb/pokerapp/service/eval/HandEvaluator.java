package com.twb.pokerapp.service.eval;

import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.repository.HandRepository;
import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class HandEvaluator {
    private final RankEvaluator rankEvaluator;
    private final HandTypeEvaluator handTypeEvaluator;
    private final HandRepository handRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public List<EvalPlayerHandDTO> evaluate(Round round, List<EvalPlayerHandDTO> playerHandList) {
        evaluateRankAndHandType(playerHandList);
        setWinners(playerHandList);
        savePlayerHandEvaluation(round, playerHandList);
        return getWinners(playerHandList);
    }

    private void evaluateRankAndHandType(List<EvalPlayerHandDTO> playerHandList) {
        for (var playerHand : playerHandList) {
            playerHand.setRank(rankEvaluator.getRank(playerHand.getCards()));
            playerHand.setHandType(handTypeEvaluator.evaluate(playerHand.getCards()));
        }
    }

    private void setWinners(List<EvalPlayerHandDTO> playerHandList) {
        playerHandList.sort(Comparator.reverseOrder());

        var winningPlayer = playerHandList.getFirst();
        var winningRankValue = winningPlayer.getRank();
        for (var playerHand : playerHandList) {
            if (Objects.equals(playerHand.getRank(), winningRankValue)) {
                playerHand.setWinner(true);
            }
        }
    }

    private void savePlayerHandEvaluation(Round round, List<EvalPlayerHandDTO> playerHandsList) {
        var savingHands = new ArrayList<Hand>();
        for (var playerHand : playerHandsList) {
            var playerSession = playerHand.getPlayerSession();
            handRepository.findHandForRound(playerSession.getId(), round.getId())
                    .ifPresent(hand -> {
                        hand.setHandType(playerHand.getHandType());
                        hand.setWinner(playerHand.isWinner());
                        savingHands.add(hand);
                    });
        }
        handRepository.saveAll(savingHands);
    }

    private List<EvalPlayerHandDTO> getWinners(List<EvalPlayerHandDTO> playerHandList) {
        return playerHandList.stream()
                .filter(EvalPlayerHandDTO::isWinner)
                .toList();
    }
}
