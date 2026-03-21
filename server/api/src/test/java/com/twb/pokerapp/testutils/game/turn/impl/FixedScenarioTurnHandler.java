package com.twb.pokerapp.testutils.game.turn.impl;

import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class FixedScenarioTurnHandler implements TurnHandler {
    @Override
    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        throw new NotImplementedException("Not implemented yet");
    }
}
