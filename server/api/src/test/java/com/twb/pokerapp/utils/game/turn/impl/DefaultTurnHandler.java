package com.twb.pokerapp.utils.game.turn.impl;

import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Arrays;

public class DefaultTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        Arrays.stream(playerTurn.getActions())
                .findFirst()
                .ifPresent(action -> {
                    CreatePlayerActionDTO createActionDto = new CreatePlayerActionDTO();
                    createActionDto.setAction(action);
                    createActionDto.setAmount(playerTurn.getAmountToCall());

                    user.sendPlayerAction(createActionDto);
                });
    }
}
