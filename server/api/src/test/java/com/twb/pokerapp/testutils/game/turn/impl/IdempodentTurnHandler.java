package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Arrays;

public class IdempodentTurnHandler implements TurnHandler {

    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        Arrays.stream(playerTurn.getNextActions())
                .findFirst()
                .ifPresent(action -> {
                    var createActionDto = new CreatePlayerActionDTO();
                    createActionDto.setAction(action);
                    createActionDto.setAmount(playerTurn.getAmountToCall());
                    user.sendPlayerAction(createActionDto);
                    user.sendPlayerAction(createActionDto);
                });
    }
}
