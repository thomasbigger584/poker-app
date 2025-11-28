package com.twb.pokerapp.utils.game.turn.impl;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Arrays;

public class OptimisticTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        var bettingRound = playerTurn.getBettingRound();
        if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.BET)) {
            var createActionDto = new CreatePlayerActionDTO();
            createActionDto.setAction(ActionType.BET);
            createActionDto.setAmount(10d);

            user.sendPlayerAction(createActionDto);
        } else if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.CALL)) {
            var createActionDto = new CreatePlayerActionDTO();
            createActionDto.setAction(ActionType.CALL);

            user.sendPlayerAction(createActionDto);
        } else {
            throw new IllegalStateException("Failed to find bet action in player turn response");
        }
    }
}
