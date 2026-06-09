package com.twb.pokerapp.testutils.game.turn;

import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.proto.CreatePlayerActionDTO;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
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
        var builder = CreatePlayerActionDTO.newBuilder();
        // A null action / amount stays as the proto3 default (UNSPECIFIED / "") so invalid-action
        // scenarios still exercise server-side validation.
        if (action != null) {
            builder.setAction(action);
        }
        if (amount != null) {
            builder.setAmount(amount.toPlainString());
        }
        user.sendPlayerAction(builder.build());
    }
}
