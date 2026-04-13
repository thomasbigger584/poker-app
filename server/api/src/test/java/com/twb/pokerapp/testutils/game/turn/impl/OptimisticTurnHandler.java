package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.twb.pokerapp.testutils.game.turn.TurnHandler.sendPlayerAction;

// Will Bet or Call when it can. It will not check or fold
public class OptimisticTurnHandler implements TurnHandler {
    private static final BigDecimal DEFAULT_BET_AMOUNT = BigDecimal.valueOf(10);

    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.BET)) {
            sendPlayerAction(user, ActionType.BET, DEFAULT_BET_AMOUNT);
        } else if (Arrays.stream(playerTurn.getNextActions())
                .anyMatch(actionType -> actionType == ActionType.CALL)) {
            sendPlayerAction(user, ActionType.CALL, playerTurn.getAmountToCall());
        } else {
            throw new IllegalStateException("Failed to find bet or call action in player turn response");
        }
    }
}
