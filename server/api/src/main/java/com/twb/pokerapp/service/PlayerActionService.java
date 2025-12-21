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
}
