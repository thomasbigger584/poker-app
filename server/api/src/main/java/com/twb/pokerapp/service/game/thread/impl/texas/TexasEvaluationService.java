package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.repository.CardRepository;
import com.twb.pokerapp.repository.HandRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.service.eval.HandEvaluator;
import com.twb.pokerapp.service.eval.dto.EvalPlayerHandDTO;
import com.twb.pokerapp.service.game.thread.GameLogService;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.WinnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.twb.pokerapp.service.game.thread.util.SleepUtil.sleepInMs;

@Component
@Transactional
@RequiredArgsConstructor
public class TexasEvaluationService {
    private final PlayerSessionRepository playerSessionRepository;
    private final HandRepository handRepository;
    private final CardRepository cardRepository;
    private final HandEvaluator handEvaluator;
    private final WinnerService winnerService;
    private final GameLogService gameLogService;

    public void evaluate(GameThreadParams params, PokerTable table, Round round) {
        var activePlayers = playerSessionRepository
                .findActivePlayersByTableId(table.getId(), round.getId());
        if (activePlayers.size() == 1) {
            var winner = activePlayers.getFirst();
            evaluateLastPlayerStanding(table, winner, round);
        } else {
            evaluateMultiPlayersStanding(table, round, activePlayers);
        }
        sleepInMs(params.getEvalWaitMs());
    }

    private void evaluateLastPlayerStanding(PokerTable table, PlayerSession winner, Round round) {
        handRepository.markHandAsWinner(round.getId(), winner.getId());
        handRepository.markHandsAsLosersWithWinner(round.getId(), winner.getId());

        gameLogService.sendLogMessage(table, "%s wins round".formatted(winner.getUser().getUsername()));
    }

    private void evaluateMultiPlayersStanding(PokerTable table, Round round, List<PlayerSession> activePlayers) {
        var communityCards = cardRepository
                .findCommunityCardsForRound(round.getId());

        var playerHandsList = new ArrayList<EvalPlayerHandDTO>();
        for (PlayerSession potentialWinner : activePlayers) {

            var playableCards = new ArrayList<>(communityCards);

            var playerHandOpt = handRepository
                    .findHandForRound(potentialWinner.getId(), round.getId());

            if (playerHandOpt.isPresent()) {
                var hand = playerHandOpt.get();

                var playerCards = cardRepository.findCardsForHand(hand.getId());
                playableCards.addAll(playerCards);

                var playerHand = new EvalPlayerHandDTO();
                playerHand.setPlayerSession(potentialWinner);
                playerHand.setCards(playableCards);
                playerHandsList.add(playerHand);
            }
        }
        var winners = handEvaluator.evaluate(round, playerHandsList);

        winnerService.handleWinners(table, winners);
    }
}
