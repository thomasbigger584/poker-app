package com.twb.pokergame.utils.game.player.impl;

import com.twb.pokergame.domain.enumeration.ConnectionType;
import com.twb.pokergame.utils.game.player.AbstractTestUser;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
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

    }

    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.PLAYER;
    }
}
