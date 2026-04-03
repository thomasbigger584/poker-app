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
        // Repository returns newest first.
        for (var action : allActions) {
            var playerId = action.getPlayerSession().getId();
            if (action.getAmount() != null) {
                playerTotalBets.merge(playerId, action.getAmount(), Double::sum);
            }
        }

        var processedPlayers = new HashSet<UUID>();
        for (var action : allActions) {
            var playerId = action.getPlayerSession().getId();
            if (!processedPlayers.contains(playerId)) {
                var isFold = action.getActionType() == ActionType.FOLD;
                playerFoldedStatus.put(playerId, isFold);
                processedPlayers.add(playerId);
            }
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
                if (player != null) {
                    var isFolded = playerFoldedStatus.getOrDefault(playerId, false);
                    contributions.add(new ContributionDTO(player, amount, isFolded));
                }
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

        var playerAllocatedToPots = new HashMap<UUID, Double>();
        for (var contribution : contributions) {
            playerAllocatedToPots.put(contribution.player().getId(), 0.0);
        }

        var previousPotLevel = 0d;

        for (ContributionDTO currentContributor : contributions) {
            var currentContributionAmount = currentContributor.amount();
            var sliceAmountPerPlayer = currentContributionAmount - previousPotLevel;

            if (sliceAmountPerPlayer <= 0.001) {
                continue;
            }

            var contributorsAtOrAboveLevel = new ArrayList<ContributionDTO>();
            var eligibleWinnersForSlice = new ArrayList<PlayerSession>();

            for (var contributor : contributions) {
                if (contributor.amount() >= currentContributionAmount) {
                    contributorsAtOrAboveLevel.add(contributor);
                    if (!contributor.isFolded()) {
                        eligibleWinnersForSlice.add(contributor.player());
                    }
                }
            }

            if (contributorsAtOrAboveLevel.size() > 1) {
                // If there's more than one player who contributed at this level, we form a pot slice.
                var totalSliceAmount = sliceAmountPerPlayer * contributorsAtOrAboveLevel.size();

                if (!eligibleWinnersForSlice.isEmpty()) {
                    distributeSliceToPots(round, roundPots, totalSliceAmount, eligibleWinnersForSlice);
                } else if (!roundPots.isEmpty()) {
                    // Everyone folded at this level, add to last pot
                    var lastPot = roundPots.getLast();
                    lastPot.setPotAmount(lastPot.getPotAmount() + totalSliceAmount);
                    roundPotRepository.save(lastPot);
                }

                // Mark as allocated for everyone who contributed to this slice
                for (var contributor : contributorsAtOrAboveLevel) {
                    playerAllocatedToPots.merge(contributor.player().getId(), sliceAmountPerPlayer, Double::sum);
                }
            }
            // If only one player contributed at this level, it's an over-bet and they aren't "allocated" to a pot.

            previousPotLevel = currentContributionAmount;
        }

        // Refund Logic: Any contributed funds not allocated to a pot are returned to the player.
        for (var contribution : contributions) {
            var playerId = contribution.player().getId();
            var totalContributed = contribution.amount();
            var allocatedToPots = playerAllocatedToPots.getOrDefault(playerId, 0.0);
            var refundAmount = totalContributed - allocatedToPots;

            if (refundAmount > 0.001) {
                var playerSession = contribution.player();
                var managedPlayerSession = playerSessionRepository.findById(playerSession.getId())
                        .orElseThrow(() -> new IllegalStateException("PlayerSession not found for refund: " + playerSession.getId()));
                managedPlayerSession.setFunds(managedPlayerSession.getFunds() + refundAmount);
                playerSessionRepository.save(managedPlayerSession);
                log.info("Refunding over-bet of {} to player {}", refundAmount, playerSession.getUser().getUsername());
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
            if (new HashSet<>(lastPot.getEligiblePlayers()).equals(new HashSet<>(eligiblePlayers))) {
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
