package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.math.BigDecimal;

import static com.twb.pokerapp.testutils.game.turn.TurnHandler.sendPlayerAction;

// Will Bet or Call when it can. It will not check or fold
public class OptimisticTurnHandler implements TurnHandler {
    private static final BigDecimal DEFAULT_BET_AMOUNT = BigDecimal.valueOf(10);

    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        var nextActions = playerTurn.getNextActionsList();
        if (nextActions.contains(ActionType.ACTION_TYPE_BET)) {
            sendPlayerAction(user, ActionType.ACTION_TYPE_BET, DEFAULT_BET_AMOUNT);
        } else if (nextActions.contains(ActionType.ACTION_TYPE_CALL)) {
            sendPlayerAction(user, ActionType.ACTION_TYPE_CALL, ProtoConvert.bigDecimal(playerTurn.getAmountToCall()));
        } else {
            throw new IllegalStateException("Failed to find bet or call action in player turn response");
        }
    }
}
