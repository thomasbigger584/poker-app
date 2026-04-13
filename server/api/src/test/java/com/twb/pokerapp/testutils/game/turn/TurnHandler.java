package com.twb.pokerapp.testutils.game.turn;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public interface TurnHandler {

    void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn);

    static Map<String, TurnHandler> of(TurnHandler... handlers) {
        var userToTurnHandlers = new HashMap<String, TurnHandler>();
        for (var index = 1; index <= handlers.length; index++) {
            userToTurnHandlers.put("user" + index, handlers[index - 1]);
        }
        return userToTurnHandlers;
    }

    static void sendPlayerAction(AbstractTestUser user, ActionType action, BigDecimal amount) {
        var createActionDto = new CreatePlayerActionDTO();
        createActionDto.setAction(action);
        createActionDto.setAmount(amount);
        user.sendPlayerAction(createActionDto);
    }
}