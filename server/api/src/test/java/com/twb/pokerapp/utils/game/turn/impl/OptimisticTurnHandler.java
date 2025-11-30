package com.twb.pokerapp.utils.game.turn.impl;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Arrays;

public class OptimisticTurnHandler implements TurnHandler {
    private static final double DEFAULT_BET_AMOUNT = 10d;

    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.BET)) {
            sendPlayerAction(user, ActionType.BET);
        } else if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.CALL)) {
            sendPlayerAction(user, ActionType.CALL);
        } else {
            throw new IllegalStateException("Failed to find bet action in player turn response");
        }
    }

    private void sendPlayerAction(AbstractTestUser user, ActionType action) {
        var createActionDto = new CreatePlayerActionDTO();
        createActionDto.setAction(action);
        createActionDto.setAmount(DEFAULT_BET_AMOUNT);
        user.sendPlayerAction(createActionDto);
    }
}
