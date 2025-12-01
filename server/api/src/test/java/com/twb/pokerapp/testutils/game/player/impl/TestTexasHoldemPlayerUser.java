package com.twb.pokerapp.testutils.game.player.impl;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;

@Slf4j
public class TestTexasHoldemPlayerUser extends AbstractTestUser {

    public TestTexasHoldemPlayerUser(TestUserParams params) {
        super(params);
    }

    @Override
    protected void handleMessage(StompHeaders headers, ServerMessageDTO message) {
        if (message.getType() == ServerMessageType.PLAYER_TURN) {
            var playerTurn = (PlayerTurnDTO) message.getPayload();
            if (playerTurn.getPlayerSession()
                    .getUser().getUsername().equals(params.getUsername())) {
                var turnHandler = params.getTurnHandler();
                if (turnHandler != null) {
                    try {
                        Thread.sleep(2000L);
                        turnHandler.handle(this, headers, playerTurn);
                    } catch (InterruptedException e) {
                        log.error("Failed to handle player turn", e);
                        throw new RuntimeException("Failed to handle player turn", e);
                    }
                }
            }
        }
    }

    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.PLAYER;
    }
}
