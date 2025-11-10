package com.twb.pokerapp.utils.game.player.impl;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.player.TestUserParams;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class TestTexasHoldemPlayerUser extends AbstractTestUser {
    private final static Logger logger = LoggerFactory.getLogger(TestTexasHoldemPlayerUser.class);

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
                    turnHandler.handle(this, headers, playerTurn);
                }
            }
        }
    }

    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.PLAYER;
    }
}
