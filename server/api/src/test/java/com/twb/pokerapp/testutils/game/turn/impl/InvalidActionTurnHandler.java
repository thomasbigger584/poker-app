package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static com.twb.pokerapp.testutils.game.turn.TurnHandler.sendPlayerAction;

public class InvalidActionTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        sendPlayerAction(user, null, -1d);
    }
}
