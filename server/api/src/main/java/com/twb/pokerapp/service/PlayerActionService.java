package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.mapper.PlayerActionMapper;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.repository.PlayerSessionRepository;
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

        return playerAction;
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    public double getAmountToCall(PlayerSession playerSession, List<PlayerAction> prevPlayerActions) {
        var playerContributions = getPlayerContributions(prevPlayerActions);
        var maxBet = playerContributions.values().stream().max(Double::compare).orElse(0d);
        var currentContribution = playerContributions.getOrDefault(playerSession.getId(), 0d);
        return Math.max(0d, maxBet - currentContribution);
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
