package com.twb.pokerapp.service.game.thread.impl.texas.bettinground;

import com.twb.pokerapp.domain.*;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.repository.*;
import com.twb.pokerapp.service.BettingRoundService;
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
    private final BettingRoundService bettingRoundService;

    @Transactional(propagation = Propagation.MANDATORY)
    public Round reconcilePots(Round round, BettingRound bettingRound) {
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

        return calculatePotSlices(round, bettingRound, contributions, sessionMap);
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
            if (action.getActionType() == ActionType.FOLD) {
                playerFoldedStatus.put(playerId, true);
            }
        }
    }

    private List<ContributionDTO> getPlayerContributions(Map<UUID, Double> playerTotalBets,
                                                         Map<UUID, PlayerSession> sessionMap,
                                                         Map<UUID, Boolean> playerFoldedStatus) {
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

    private Round calculatePotSlices(Round round, BettingRound bettingRound,
                                     List<ContributionDTO> contributions,
                                     Map<UUID, PlayerSession> sessionMap) {
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

            // 1. Is this slice "Contested"? (More than one person put money in at this level)
            var isContested = contributorsAtOrAboveLevel.size() > 1;

            // 2. Is this the ONLY money in the round? (Handles "Single bet wins immediately")
            // If the pot is currently empty and this is the first slice, we must count it.
            var isInitialBet = roundPots.isEmpty();

            // 3. Special Case: If everyone folded to a bet, the "Uncalled" part of that bet
            // should only be added to the pot if it was actually called/matched.
            // In your "Ace-high" scenario:
            // At the 5000 level, only User 1 exists. isContested = false.
            // Since roundPots is NOT empty (it has the 1k and 2k levels), this is NOT added.

            if (isContested || isInitialBet) {
                var totalSliceAmount = sliceAmountPerPlayer * contributorsAtOrAboveLevel.size();

                if (!eligibleWinnersForSlice.isEmpty()) {
                    distributeSliceToPots(round, roundPots, totalSliceAmount, eligibleWinnersForSlice);

                    for (var contributor : contributorsAtOrAboveLevel) {
                        playerAllocatedToPots.merge(contributor.player().getId(), sliceAmountPerPlayer, Double::sum);
                    }
                }
            }

            previousPotLevel = currentContributionAmount;
        }

        // Refund Logic
        for (var contribution : contributions) {
            var playerId = contribution.player().getId();
            var totalContributed = contribution.amount();
            var allocatedToPots = playerAllocatedToPots.getOrDefault(playerId, 0.0);
            var refundAmount = totalContributed - allocatedToPots;

            if (refundAmount > 0.001) {
                var managedPlayerSession = sessionMap.get(playerId);
                managedPlayerSession.setFunds(managedPlayerSession.getFunds() + refundAmount);
                playerSessionRepository.save(managedPlayerSession);

                bettingRoundService.createRefund(managedPlayerSession, bettingRound, refundAmount);
                log.info("Refunding uncalled/over-bet portion of {} to player {}", refundAmount, managedPlayerSession.getUser().getUsername());
            }
        }

        round.setRoundPots(roundPots);
        return roundRepository.save(round);
    }

    private void distributeSliceToPots(Round round,
                                       List<RoundPot> roundPots,
                                       double amount,
                                       List<PlayerSession> eligiblePlayers) {
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