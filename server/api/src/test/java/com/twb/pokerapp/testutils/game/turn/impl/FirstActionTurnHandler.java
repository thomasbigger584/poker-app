package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import static com.twb.pokerapp.testutils.game.turn.TurnHandler.sendPlayerAction;

public class FirstActionTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        playerTurn.getNextActionsList().stream()
                .findFirst()
                .ifPresent(action -> sendPlayerAction(user, action, ProtoConvert.bigDecimal(playerTurn.getAmountToCall())));
    }
}
