package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.mapper.PlayerActionMapper;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
import com.twb.pokerapp.service.game.thread.impl.texas.dto.NextActionsDTO;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerActionService {
    private final PlayerSessionRepository playerSessionRepository;
    private final PlayerActionRepository repository;
    private final PlayerActionMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public PlayerAction create(PlayerSession playerSession, BettingRound bettingRound, CreatePlayerActionDTO createDto) {
        var amount = createDto.getAmount();
        if (amount != null && amount > 0d) {
            playerSession.setFunds(playerSession.getFunds() - amount);
            playerSession = playerSessionRepository.save(playerSession);
        }

        var playerAction = new PlayerAction();
        playerAction.setPlayerSession(playerSession);
        playerAction.setBettingRound(bettingRound);
        playerAction.setActionType(createDto.getAction());
        playerAction.setAmount(createDto.getAmount());

        playerAction = repository.save(playerAction);

        if (playerAction.getActionType() == ActionType.FOLD) {
            playerSession.setActive(false);
            playerSessionRepository.save(playerSession);
        }

        return playerAction;
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public NextActionsDTO getNextActions(PlayerSession playerSession, List<PlayerAction> prevPlayerActions) {
        var nextActions = ActionType.getDefaultActions();
        var amountToCall = 0d;
        if (!prevPlayerActions.isEmpty()) {
            var previousPlayerAction = prevPlayerActions.getFirst();
            var previousPlayerActionType = previousPlayerAction.getActionType();
            nextActions = previousPlayerActionType.getNextActions();
            amountToCall = getAmountToCall(playerSession, prevPlayerActions);
        }
        if (amountToCall > playerSession.getFunds()) {
            nextActions = ActionType.getAllInActions();
        }
        return new NextActionsDTO(amountToCall, nextActions);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public double getAmountToCall(PlayerSession playerSession, List<PlayerAction> prevPlayerActions) {
        var playerContributions = getPlayerContributions(prevPlayerActions);
        var maxBet = playerContributions.values().stream().max(Double::compare).orElse(0d);
        var currentContribution = playerContributions.getOrDefault(playerSession.getId(), 0d);
        return Math.max(0d, maxBet - currentContribution);
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public boolean isAggressive(PlayerAction playerAction) {
        var bettingRound = playerAction.getBettingRound();
        var actionType = playerAction.getActionType();
        var isAggressive = actionType.isAggressive();

        // If All-In, we must check if it was a Raise (Aggressive) or a Call (Passive)
        // by comparing the amount against the max bet of other players.

        // scenario where a player has already put money in the pot (e.g., a Call) and then later goes All-In for a raise.
        // 1. Player A Bets 100.
        // 2. Player B Calls 100.
        // 3. Player C Raises to 300 (Delta: 300).
        // 4. Player A Calls 200 (Total: 300).
        // 5. Player B goes All-In for 400 Total (Delta: 300).

        if (!isAggressive && actionType == ActionType.ALL_IN) {
            var actions = repository.findPlayerActionsNotFolded(bettingRound.getId());
            var playerContributions = getPlayerContributions(actions);

            var currentPlayerId = playerAction.getPlayerSession().getId();
            var currentPlayerTotal = playerContributions.getOrDefault(currentPlayerId, 0d);
            var maxOtherTotal = playerContributions.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(currentPlayerId))
                    .mapToDouble(Map.Entry::getValue)
                    .max()
                    .orElse(0d);
            if (currentPlayerTotal > maxOtherTotal) {
                isAggressive = true;
            }
        }
        return isAggressive;
    }

    private Map<UUID, Double> getPlayerContributions(List<PlayerAction> prevPlayerActions) {
        var playerContributions = new HashMap<UUID, Double>();
        for (var action : prevPlayerActions) {
            var thisAmount = action.getAmount() == null ? 0d : action.getAmount();
            playerContributions.merge(action.getPlayerSession().getId(), thisAmount, Double::sum);
        }
        return playerContributions;
    }
}
