package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Arrays;

import static com.twb.pokerapp.testutils.game.turn.TurnHandler.sendPlayerAction;

public class AggresiveTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.RAISE)) {
            sendPlayerAction(user, ActionType.RAISE, playerTurn.getAmountToCall() * 2);
        } else if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.CALL)) {
            sendPlayerAction(user, ActionType.CALL, playerTurn.getAmountToCall());
        } else if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.CHECK)) {
            sendPlayerAction(user, ActionType.CALL, 0d);
        } else {
            throw new IllegalStateException("Failed to find bet action in player turn response");
        }
    }
}
