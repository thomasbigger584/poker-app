package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.RoundPot;
import com.twb.pokerapp.repository.*;
import com.twb.pokerapp.service.game.eval.HandEvaluator;
import com.twb.pokerapp.service.game.eval.dto.EvalPlayerHandDTO;
import com.twb.pokerapp.service.game.thread.GameLogService;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.twb.pokerapp.util.SleepUtil.sleepInMs;
import static com.twb.pokerapp.util.TransactionUtil.afterCommit;

@Component
@RequiredArgsConstructor
public class TexasEvaluationService {
    private final TransactionTemplate writeTx;
    private final RoundRepository roundRepository;
    private final RoundPotRepository roundPotRepository;
    private final PlayerSessionRepository playerSessionRepository;
    private final HandRepository handRepository;
    private final CardRepository cardRepository;
    private final HandEvaluator handEvaluator;
    private final GameLogService gameLogService;

    public void evaluate(GameThreadParams params) {
        writeTx.executeWithoutResult(status -> {
            var roundOpt = roundRepository.findCurrentByTableId(params.getTableId());
            if (roundOpt.isEmpty()) {
                gameLogService.sendLogMessage(params.getTableId(), "Round not found for table: " + params.getTableId());
                return;
            }
            var round = roundOpt.get();
            var activePlayers = playerSessionRepository
                    .findActivePlayersByTableId(params.getTableId(), round.getId());
            if (activePlayers.size() == 1) {
                var winner = activePlayers.getFirst();
                evaluateLastPlayerStanding(params, winner, round);
            } else {
                evaluateMultiPlayersStanding(params, round, activePlayers);
            }
        });
        sleepInMs(params.getEvalWaitMs());
    }

    private void evaluateLastPlayerStanding(GameThreadParams params, PlayerSession winner, Round round) {
        var pots = roundPotRepository.findByRound(round.getId());
        var totalWinnings = 0.0;
        for (var pot : pots) {
            totalWinnings += pot.getPotAmount();
        }

        winner.setFunds(winner.getFunds() + totalWinnings);
        playerSessionRepository.save(winner);

        var winnerUsername = winner.getUser().getUsername();
        var finalTotalWinnings = totalWinnings;
        afterCommit(() -> gameLogService.sendLogMessage(params.getTableId(), "%s wins round with $%.2f".formatted(winnerUsername, finalTotalWinnings)));
    }

    private void evaluateMultiPlayersStanding(GameThreadParams params, Round round, List<PlayerSession> activePlayers) {
        var communityCards = cardRepository.findCommunityCardsForRound(round.getId());

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

        handEvaluator.evaluate(round, playerHandsList);

        var pots = roundPotRepository.findByRound(round.getId());
        for (var pot : pots) {
            handlePotDistribution(params, round, pot, playerHandsList);
        }
    }

    private void handlePotDistribution(GameThreadParams params, Round round, RoundPot pot, List<EvalPlayerHandDTO> allEvaluatedHands) {
        var eligiblePlayers = pot.getEligiblePlayers();
        var eligiblePlayerIds = eligiblePlayers.stream().map(PlayerSession::getId).toList();

        // Filter hands for this pot, preserving order (best to worst)
        var potHands = allEvaluatedHands.stream()
                .filter(hand -> eligiblePlayerIds.contains(hand.getPlayerSession().getId()))
                .toList();

        if (potHands.isEmpty()) return;

        var winners = new ArrayList<EvalPlayerHandDTO>();
        var bestHand = potHands.getFirst();
        winners.add(bestHand);

        for (var index = 1; index < potHands.size(); index++) {
            var nextHand = potHands.get(index);
            if (nextHand.compareTo(bestHand) == 0) {
                winners.add(nextHand);
            } else {
                break; // Since sorted, once we find a worse hand, we stop
            }
        }

        distributePotToWinners(params, round, pot, winners);
    }

    private void distributePotToWinners(GameThreadParams params, Round round, RoundPot pot, List<EvalPlayerHandDTO> winners) {
        var potAmount = pot.getPotAmount();
        var splitAmount = potAmount / winners.size();

        for (var winnerHand : winners) {
            var playerSession = winnerHand.getPlayerSession();
            playerSession.setFunds(playerSession.getFunds() + splitAmount);
            playerSessionRepository.save(playerSession);
        }

        logPotWinners(params, pot, winners, splitAmount);
    }

    private void logPotWinners(GameThreadParams params, RoundPot pot, List<EvalPlayerHandDTO> winners, double amountPerWinner) {
        var potName = (pot.getPotIndex() == 0) ? "Main Pot" : "Side Pot " + pot.getPotIndex();
        var winnerNames = getReadableWinners(winners);
        var handTypeStr = winners.getFirst().getHandType().getValue();

        String message;
        if (winners.size() == 1) {
            message = "%s wins %s ($%.2f) with a %s".formatted(winnerNames, potName, pot.getPotAmount(), handTypeStr);
        } else {
            message = "%s split %s ($%.2f) with a %s (Each gets $%.2f)".formatted(winnerNames, potName, pot.getPotAmount(), handTypeStr, amountPerWinner);
        }
        afterCommit(() -> gameLogService.sendLogMessage(params.getTableId(), message));
    }

    private String getReadableWinners(List<EvalPlayerHandDTO> winners) {
        var sb = new StringBuilder();
        for (var index = 0; index < winners.size(); index++) {
            var eval = winners.get(index);
            var user = eval.getPlayerSession().getUser();
            sb.append(user.getUsername());
            if (index < winners.size() - 2) {
                sb.append(", ");
            } else if (index == winners.size() - 2) {
                sb.append(" & ");
            }
        }
        return sb.toString();
    }
}
