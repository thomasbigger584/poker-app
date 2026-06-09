package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.math.BigDecimal;

import static com.twb.pokerapp.testutils.game.turn.TurnHandler.sendPlayerAction;

public class AggresiveTurnHandler implements TurnHandler {
    private static final BigDecimal DEFAULT_BET_AMOUNT = BigDecimal.valueOf(10);

    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        var nextActions = playerTurn.getNextActionsList();
        var amountToCall = ProtoConvert.bigDecimal(playerTurn.getAmountToCall());
        var callAmount = amountToCall == null ? BigDecimal.ZERO : amountToCall;
        if (nextActions.contains(ActionType.ACTION_TYPE_RAISE)) {
            sendPlayerAction(user, ActionType.ACTION_TYPE_RAISE, callAmount.multiply(BigDecimal.valueOf(2)));
        } else if (nextActions.contains(ActionType.ACTION_TYPE_BET)) {
            sendPlayerAction(user, ActionType.ACTION_TYPE_BET, DEFAULT_BET_AMOUNT);
        } else if (nextActions.contains(ActionType.ACTION_TYPE_CALL)) {
            sendPlayerAction(user, ActionType.ACTION_TYPE_CALL, callAmount);
        } else if (nextActions.contains(ActionType.ACTION_TYPE_CHECK)) {
            sendPlayerAction(user, ActionType.ACTION_TYPE_CHECK, BigDecimal.ZERO);
        } else {
            throw new IllegalStateException("Failed to find action in player turn response");
        }
    }
}
