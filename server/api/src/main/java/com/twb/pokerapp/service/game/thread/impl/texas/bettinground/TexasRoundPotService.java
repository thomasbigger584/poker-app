package com.twb.pokerapp.service.game.thread.impl.texas.bettinground;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.RoundPot;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.repository.RoundPotRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.service.game.thread.dto.ContributionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TexasRoundPotService {
    private final RoundRepository roundRepository;
    private final PlayerActionRepository playerActionRepository;
    private final PlayerSessionRepository playerSessionRepository;
    private final RoundPotRepository roundPotRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Round reconcilePots(Round round) {
        var playerTotalBets = new HashMap<UUID, Double>();
        var playerFoldedStatus = new HashMap<UUID, Boolean>();
        var sessionMap = new HashMap<UUID, PlayerSession>();

        var playerSessions = playerSessionRepository.findPlayersOnRound(round.getId());

        for (var session : playerSessions) {
            playerTotalBets.put(session.getId(), 0.0);
            playerFoldedStatus.put(session.getId(), false);
            sessionMap.put(session.getId(), session);
        }

        sumAmountsFromActions(round.getId(), playerTotalBets, playerFoldedStatus);

        var contributions = getPlayerContributions(playerTotalBets, sessionMap, playerFoldedStatus);

        return calculatePotSlices(round, contributions);
    }

    private void sumAmountsFromActions(UUID roundId,
                                       Map<UUID, Double> playerTotalBets,
                                       Map<UUID, Boolean> playerFoldedStatus) {
        var allActions = playerActionRepository.findByRoundId(roundId);
        for (var action : allActions) {
            var playerId = action.getPlayerSession().getId();
            if (action.getAmount() != null) {
                playerTotalBets.merge(playerId, action.getAmount(), Double::sum);
            }
            var isFold = action.getActionType() == ActionType.FOLD;
            playerFoldedStatus.put(playerId, isFold);
        }
    }

    private List<ContributionDTO> getPlayerContributions(Map<UUID, Double> playerTotalBets,
                                                         Map<UUID, PlayerSession> sessionMap,
                                                         Map<UUID, Boolean> playerFoldedStatus
    ) {
        var contributions = new ArrayList<ContributionDTO>();
        for (var entry : playerTotalBets.entrySet()) {
            var playerId = entry.getKey();
            var amount = entry.getValue();
            if (amount > 0) {
                var player = sessionMap.get(playerId);
                var isFolded = playerFoldedStatus.get(playerId);
                contributions.add(new ContributionDTO(player, amount, isFolded));
            }
        }
        Collections.sort(contributions);
        return contributions;
    }

    private Round calculatePotSlices(Round round, List<ContributionDTO> contributions) {
        var roundPots = roundPotRepository.findByRound(round.getId());
        if (CollectionUtils.isNotEmpty(roundPots)) {
            roundPotRepository.deleteAll(roundPots);
            roundPots.clear();
        }

        // Map to store how much each player has contributed to pots
        var playerAllocatedToPots = new HashMap<UUID, Double>();
        for (var contribution : contributions) {
            playerAllocatedToPots.put(contribution.player().getId(), 0.0);
        }

        // Sort contributions by amount (ascending)
        Collections.sort(contributions);

        var previousPotLevel = 0d; // Represents the total amount contributed to previous pots by each player

        for (ContributionDTO currentContributor : contributions) {
            var currentContributionAmount = currentContributor.amount();

            // The amount that forms this pot slice, per player
            var sliceAmount = currentContributionAmount - previousPotLevel;

            if (sliceAmount <= 0) {
                // This player has already been fully accounted for in previous pots,
                // or their contribution is less than or equal to the previous pot level.
                // Or, if multiple players have the same contribution amount, only the first one
                // will trigger a new slice.
                continue;
            }

            // Determine the players eligible for this slice.
            var eligiblePlayersForSlice = new ArrayList<PlayerSession>();
            for (var contributor : contributions) {
                // A player is eligible if their total contribution is at least the currentContributionAmount
                // and they haven't folded.
                if (contributor.amount() >= currentContributionAmount && !contributor.isFolded()) {
                    eligiblePlayersForSlice.add(contributor.player());
                }
            }

            if (eligiblePlayersForSlice.size() > 1) {
                // If more than one player is eligible, form a pot
                var currentPotTotal = sliceAmount * eligiblePlayersForSlice.size();
                distributeSliceToPots(round, roundPots, currentPotTotal, eligiblePlayersForSlice);

                // Update allocated amounts for players who contributed to this slice
                for (var player : eligiblePlayersForSlice) {
                    playerAllocatedToPots.merge(player.getId(), sliceAmount, Double::sum);
                }
            } else if (eligiblePlayersForSlice.size() == 1) {
                // Only one player is eligible for this slice, meaning they over-bet.
                // This amount should be refunded to that player.
                var playerToRefund = eligiblePlayersForSlice.getFirst();
                var refundAmount = sliceAmount;

                // Update the player's funds directly
                var managedPlayerSession = playerSessionRepository.findById(playerToRefund.getId())
                        .orElseThrow(() -> new IllegalStateException("PlayerSession not found for refund: " + playerToRefund.getId()));
                managedPlayerSession.setFunds(managedPlayerSession.getFunds() + refundAmount);
                playerSessionRepository.save(managedPlayerSession);
                log.info("Refunding {} to player {}", refundAmount, playerToRefund.getUser().getUsername());

                // The amount is refunded, so it's not allocated to a pot.
                // playerAllocatedToPots is NOT updated for this player for this sliceAmount.
            }
            // If eligiblePlayersForSlice is empty, currentPotTotal would be 0, so nothing to do.

            previousPotLevel = currentContributionAmount;
        }

        // Final check for any remaining unallocated funds.
        // This should ideally be 0 if the logic is perfect.
        for (var contribution : contributions) {
            var playerId = contribution.player().getId();
            var totalContributed = contribution.amount();
            var allocatedToPots = playerAllocatedToPots.getOrDefault(playerId, 0.0);
            var refundAmount = totalContributed - allocatedToPots;

            if (refundAmount > 0) {
                var playerSession = contribution.player();
                var managedPlayerSession = playerSessionRepository.findById(playerSession.getId())
                        .orElseThrow(() -> new IllegalStateException("PlayerSession not found for refund: " + playerSession.getId()));
                managedPlayerSession.setFunds(managedPlayerSession.getFunds() + refundAmount);
                playerSessionRepository.save(managedPlayerSession);
                log.warn("Refunding (final check, unexpected) {} to player {}", refundAmount, playerSession.getUser().getUsername());
            }
        }

        round.setRoundPots(roundPots);
        return roundRepository.save(round);
    }

    private void distributeSliceToPots(Round round,
                                       List<RoundPot> roundPots,
                                       double amount,
                                       List<PlayerSession> eligiblePlayers
    ) {
        if (!roundPots.isEmpty()) {
            var lastPot = roundPots.getLast();
            if (CollectionUtils.isEqualCollection(lastPot.getEligiblePlayers(), eligiblePlayers)) {
                lastPot.setPotAmount(lastPot.getPotAmount() + amount);
                roundPotRepository.save(lastPot);
                return;
            }
        }

        var roundPot = new RoundPot();
        roundPot.setRound(round);
        roundPot.setPotAmount(amount);
        roundPot.setEligiblePlayers(new ArrayList<>(eligiblePlayers));
        roundPot.setPotIndex(roundPots.size());

        roundPot = roundPotRepository.save(roundPot);
        roundPots.add(roundPot);
    }
}
