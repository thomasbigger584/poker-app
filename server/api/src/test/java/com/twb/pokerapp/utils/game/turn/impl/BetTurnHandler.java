package com.twb.pokerapp.utils.game.turn.impl;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Arrays;

public class BetTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {

        if (Arrays.stream(playerTurn.getActions())
                .anyMatch(actionType -> actionType == ActionType.BET)) {

            CreatePlayerActionDTO createActionDto = new CreatePlayerActionDTO();
            createActionDto.setAction(ActionType.BET);
            createActionDto.setAmount(10d);
            user.sendPlayerAction(createActionDto);
        } else {
            throw new IllegalStateException("Failed to find bet action in player turn response");
        }

    }
}
