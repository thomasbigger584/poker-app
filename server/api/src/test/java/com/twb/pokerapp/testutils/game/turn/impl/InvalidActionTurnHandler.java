package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class InvalidActionTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        var createActionDto = new CreatePlayerActionDTO();
        createActionDto.setAmount(-1d);
        user.sendPlayerAction(createActionDto);
    }
}
