package com.twb.pokerapp.service.game.thread.impl.texas;

import com.twb.pokerapp.domain.*;
import com.twb.pokerapp.repository.*;
import com.twb.pokerapp.service.game.eval.HandEvaluator;
import com.twb.pokerapp.service.game.eval.dto.EvalPlayerHandDTO;
import com.twb.pokerapp.service.game.thread.GameLogService;
import com.twb.pokerapp.service.game.thread.GameSpeedService;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    private final RoundWinnerRepository roundWinnerRepository;
    private final HandEvaluator handEvaluator;
    private final GameLogService gameLogService;
    private final GameSpeedService gameSpeedService;

    public void evaluate(GameThreadParams params) {
        writeTx.executeWithoutResult(status -> {
            var roundOpt = roundRepository.findCurrentByTableId(params.getTableId());
            if (roundOpt.isEmpty()) {
                gameLogService.sendLogMessage(params.getTableId(), "Round not found for table: " + params.getTableId());
                return;
            }
            var round = roundOpt.get();
            var activePlayers = playerSessionRepository.findActivePlayersByTableId(params.getTableId(), round.getId());
            if (activePlayers.size() == 1) {
                var winner = activePlayers.getFirst();
                evaluateLastPlayerStanding(params, winner, round);
            } else {
                evaluateMultiPlayersStanding(params, round, activePlayers);
            }
        });
        gameSpeedService.sleep(params.getEvalWaitMs());
    }

    private void evaluateLastPlayerStanding(GameThreadParams params, PlayerSession winner, Round round) {
        var pots = roundPotRepository.findByRound(round.getId());
        var totalWinnings = pots.stream()
                .mapToDouble(RoundPot::getPotAmount)
                .sum();
        winner.setFunds(winner.getFunds() + totalWinnings);
        playerSessionRepository.save(winner);

        var hand = handRepository.findForPlayerAndRound(winner.getId(), round.getId())
                .orElse(null);
        
        saveRoundWinner(winner, round, hand, totalWinnings);

        var winnerUsername = winner.getUser().getUsername();
        afterCommit(() -> gameLogService.sendLogMessage(params.getTableId(), "%s wins round with $%.2f"
                .formatted(winnerUsername, totalWinnings)));
    }

    private void evaluateMultiPlayersStanding(GameThreadParams params, Round round, List<PlayerSession> activePlayers) {
        var communityCards = cardRepository.findCommunityCardsForRound(round.getId());

        var evalPlayerHandsList = new ArrayList<EvalPlayerHandDTO>();
        for (var potentialWinner : activePlayers) {
            var playerHandOpt = handRepository.findForPlayerAndRound(potentialWinner.getId(), round.getId());
            if (playerHandOpt.isPresent()) {
                var hand = playerHandOpt.get();
                var playableCards = new ArrayList<>(communityCards);

                var playerCards = cardRepository.findCardsForHand(hand.getId());
                playableCards.addAll(playerCards);

                var evalPlayerHand = new EvalPlayerHandDTO();
                evalPlayerHand.setPlayerSession(potentialWinner);
                evalPlayerHand.setCards(playableCards);
                evalPlayerHand.setHand(hand);
                evalPlayerHandsList.add(evalPlayerHand);
            }
        }

        handEvaluator.evaluate(round, evalPlayerHandsList);

        var pots = roundPotRepository.findByRound(round.getId());
        for (var pot : pots) {
            handlePotDistribution(params, round, pot, evalPlayerHandsList);
        }
    }

    private void handlePotDistribution(GameThreadParams params, Round round,
                                       RoundPot pot, List<EvalPlayerHandDTO> evalPlayerHandsList) {
        var eligiblePlayers = pot.getEligiblePlayers();
        var eligiblePlayerIds = eligiblePlayers.stream()
                .map(PlayerSession::getId).toList();

        var potHands = evalPlayerHandsList.stream()
                .filter(hand ->
                        eligiblePlayerIds.contains(hand.getPlayerSession().getId()))
                .sorted(Comparator.naturalOrder())
                .toList();

        if (potHands.isEmpty()) return;

        var winners = new ArrayList<EvalPlayerHandDTO>();
        var bestHand = potHands.getFirst();
        winners.add(bestHand);

        // Check for ties
        for (var index = 1; index < potHands.size(); index++) {
            var nextHand = potHands.get(index);
            if (nextHand.compareTo(bestHand) == 0) {
                winners.add(nextHand);
            } else {
                break;
            }
        }

        distributePotToWinners(params, round, pot, winners);
    }

    private void distributePotToWinners(GameThreadParams params, Round round, RoundPot pot, List<EvalPlayerHandDTO> winners) {
        var potAmount = pot.getPotAmount();
        var winnerCount = winners.size();
        if (winnerCount == 0) return;

        // Calculate split in cents to handle odd chips correctly
        var totalCents = Math.round(potAmount * 100);
        var splitCents = totalCents / winnerCount;
        var remainderCents = totalCents % winnerCount;
        var splitAmount = splitCents / 100.0;

        for (var index = 0; index < winnerCount; index++) {
            var winnerHand = winners.get(index);
            // Add 1 cent to the award amount for each winner until the remainder is exhausted
            var extraCent = (index < remainderCents) ? 0.01 : 0.0;
            var awardAmount = splitAmount + extraCent;

            var playerSession = winnerHand.getPlayerSession();
            playerSession.setFunds(playerSession.getFunds() + awardAmount);
            playerSessionRepository.save(playerSession);

            saveRoundWinner(playerSession, round, winnerHand.getHand(), awardAmount);
        }

        afterCommit(() -> logPotWinners(params, pot, winners, splitAmount));
    }

    private void saveRoundWinner(PlayerSession playerSession, Round round,
                                 Hand hand, double amount) {
        var roundWinnerOpt = roundWinnerRepository
                .findByRoundAndPlayerSession(round.getId(), playerSession.getId());
        RoundWinner roundWinner;
        if (roundWinnerOpt.isPresent()) {
            roundWinner = roundWinnerOpt.get();
            roundWinner.setAmount(roundWinner.getAmount() + amount);
        } else {
            roundWinner = new RoundWinner();
            roundWinner.setPlayerSession(playerSession);
            roundWinner.setRound(round);
            roundWinner.setHand(hand);
            roundWinner.setAmount(amount);
        }
        roundWinnerRepository.save(roundWinner);
    }

    private void logPotWinners(GameThreadParams params, RoundPot pot,
                               List<EvalPlayerHandDTO> winners, double amountPerWinner) {
        var potName = (pot.getPotIndex() == 0) ? "Main Pot" : "Side Pot " + pot.getPotIndex();
        var winnerNames = getReadableWinners(winners);
        
        var handTypeStr = "Unknown";
        if (!winners.isEmpty() && winners.getFirst().getHandType() != null) {
            handTypeStr = winners.getFirst().getHandType().getValue();
        }

        String message;
        if (winners.size() == 1) {
            message = "%s wins %s ($%.2f) with a %s".formatted(winnerNames, potName, pot.getPotAmount(), handTypeStr);
        } else {
            message = "%s split %s ($%.2f) with a %s (Each gets ~$%.2f)".formatted(winnerNames, potName, pot.getPotAmount(), handTypeStr, amountPerWinner);
        }
        gameLogService.sendLogMessage(params.getTableId(), message);
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
