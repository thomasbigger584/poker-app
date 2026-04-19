package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Arrays;

import static com.twb.pokerapp.testutils.game.turn.TurnHandler.sendPlayerAction;

public class AllInTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.ALL_IN)) {
            sendPlayerAction(user, ActionType.ALL_IN, playerTurn.getPlayerSession().getFunds());
        } else {
            throw new IllegalStateException("Failed to find All-In action in player turn response");
        }
    }
}
