package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.dto.playeraction.PlayerActionDTO;
import com.twb.pokerapp.mapper.PlayerActionMapper;
import com.twb.pokerapp.repository.PlayerActionRepository;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class PlayerActionService {
    private final PlayerActionRepository repository;
    private final PlayerActionMapper mapper;

    public PlayerActionDTO create(PlayerSession playerSession,
                                  BettingRound bettingRound,
                                  CreatePlayerActionDTO createDto) {

        PlayerAction playerAction = new PlayerAction();
        playerAction.setPlayerSession(playerSession);
        playerAction.setBettingRound(bettingRound);
        playerAction.setActionType(createDto.getAction());
        playerAction.setAmount(createDto.getAmount());

        repository.save(playerAction);

        return mapper.modelToDto(playerAction);
    }
}
