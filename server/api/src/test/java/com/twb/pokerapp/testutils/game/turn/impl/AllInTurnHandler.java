package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static com.twb.pokerapp.testutils.game.turn.TurnHandler.sendPlayerAction;

public class AllInTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        if (playerTurn.getNextActionsList().contains(ActionType.ACTION_TYPE_ALL_IN)) {
            sendPlayerAction(user, ActionType.ACTION_TYPE_ALL_IN,
                    ProtoConvert.bigDecimal(playerTurn.getPlayerSession().getFunds()));
        } else {
            throw new IllegalStateException("Failed to find All-In action in player turn response");
        }
    }
}
