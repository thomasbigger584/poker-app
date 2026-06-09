package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.math.BigDecimal;

import static com.twb.pokerapp.testutils.game.turn.TurnHandler.sendPlayerAction;

public class InvalidActionTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        // No action (UNSPECIFIED) + a negative amount: the server rejects the unspecified action
        // first, emitting a single "action" validation field.
        sendPlayerAction(user, null, BigDecimal.valueOf(-1));
    }
}
