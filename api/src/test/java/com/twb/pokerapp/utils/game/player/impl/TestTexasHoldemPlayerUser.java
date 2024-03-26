package com.twb.pokerapp.utils.game.player.impl;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.UUID;

public class TestTexasHoldemPlayerUser extends AbstractTestUser {
    private final static Logger logger = LoggerFactory.getLogger(TestTexasHoldemPlayerUser.class);

    public TestTexasHoldemPlayerUser(UUID tableId, CountdownLatches latches, String username, String password) {
        super(tableId, latches, username, password);
    }

    @Override
    protected void handleMessage(StompHeaders headers, ServerMessageDTO message) {
        if (message.getType() == ServerMessageType.PLAYER_TURN) {
            PlayerTurnDTO playerTurn = (PlayerTurnDTO) message.getPayload();
            if (playerTurn.getPlayerSession().getUser().getUsername().equals(username)) {
                handlePlayerTurnMessage(headers, playerTurn);
            }
        }
    }

    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.PLAYER;
    }

    /*
     * Overridable Methods
     */
    protected void handlePlayerTurnMessage(StompHeaders headers, PlayerTurnDTO playerTurn) {
    }
}
