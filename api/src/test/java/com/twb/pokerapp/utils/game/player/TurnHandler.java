package com.twb.pokerapp.utils.game.player;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Arrays;
import java.util.Optional;

public class TurnHandler {

    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        ActionType[] actions = playerTurn.getActions();
        Optional<ActionType> actionOpt = Arrays.stream(actions).findFirst();

        if (actionOpt.isPresent()) {
            ActionType action = actionOpt.get();
            CreatePlayerActionDTO createDto = new CreatePlayerActionDTO();
            createDto.setAction(action);
            user.sendPlayerAction(createDto);
        }
    }
}