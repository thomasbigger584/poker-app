package com.twb.pokerapp.service.game.thread.impl.texas.bettinground;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.domain.RoundPot;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.repository.RoundRepository;
import com.twb.pokerapp.service.game.thread.dto.ContributionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.twb.pokerapp.repository.RepositoryUtil.getThrowGameInterrupted;

@Slf4j
@Service
@RequiredArgsConstructor
public class TexasPotService {
    private final RoundRepository roundRepository;
    private final PlayerActionRepository playerActionRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void reconcilePots(Round round) {
        var playerTotalBets = new HashMap<UUID, Double>();
        var playerFoldedStatus = new HashMap<UUID, Boolean>();
        var sessionMap = new HashMap<UUID, PlayerSession>();

        // Initialize active players
        for (var session : round.getPlayerSessions()) {
            playerTotalBets.put(session.getId(), 0.0);
            playerFoldedStatus.put(session.getId(), false);
            sessionMap.put(session.getId(), session);
        }

        sumAmountsFromActions(round.getId(), playerTotalBets, playerFoldedStatus);
        var contributions = getPlayerContributions(playerTotalBets, sessionMap, playerFoldedStatus);
        calculatePotSlices(round, contributions);
    }

    private void sumAmountsFromActions(UUID roundId, HashMap<UUID, Double> playerTotalBets, HashMap<UUID, Boolean> playerFoldedStatus) {
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

    private List<ContributionDTO> getPlayerContributions(Map<UUID, Double> playerTotalBets, Map<UUID, PlayerSession> sessionMap, Map<UUID, Boolean> playerFoldedStatus) {
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

    private void calculatePotSlices(Round round, List<ContributionDTO> contributions) {
        round.getRoundPots().clear();
        var previousAmount = 0d;

        for (var index = 0; index < contributions.size(); index++) {
            var current = contributions.get(index);
            var stepAmount = current.getAmount() - previousAmount;

            if (stepAmount <= 0) {
                continue;
            }
            // Calculate the total money for this slice
            // (stepAmount * number of players who contributed at least this much)
            var sliceTotalMoney = 0.0;
            var eligiblePlayers = new ArrayList<PlayerSession>();
            for (var j = index; j < contributions.size(); j++) {
                var contributor = contributions.get(j);
                sliceTotalMoney += stepAmount;

                // Only non-folded players are eligible to win this slice
                if (!contributor.isFolded()) {
                    eligiblePlayers.add(contributor.getPlayer());
                }
            }
            if (sliceTotalMoney > 0) {
                distributeSliceToPots(round, sliceTotalMoney, eligiblePlayers);
            }
            previousAmount = current.getAmount();
        }
        roundRepository.save(round);
    }

    private void distributeSliceToPots(Round round, double amount, List<PlayerSession> eligiblePlayers) {
        var pots = round.getRoundPots();

        // Check if we can merge into the last created pot (if eligible players are identical)
        if (!pots.isEmpty()) {
            var lastPot = pots.getLast();
            if (CollectionUtils.isEqualCollection(lastPot.getEligiblePlayers(), eligiblePlayers)) {
                lastPot.setPotAmount(lastPot.getPotAmount() + amount);
                return;
            }
        }

        // Create new Pot
        var newPot = new RoundPot();
        newPot.setRound(round);
        newPot.setPotAmount(amount);
        newPot.setEligiblePlayers(new ArrayList<>(eligiblePlayers));
        newPot.setPotIndex(pots.size()); // Auto-increment index based on list size

        pots.add(newPot);
    }
}
