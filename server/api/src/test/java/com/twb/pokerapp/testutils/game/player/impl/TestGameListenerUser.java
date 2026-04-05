package com.twb.pokerapp.testutils.game.player.impl;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.GameFinishedDTO;
import lombok.extern.slf4j.Slf4j;
import org.opentest4j.AssertionFailedError;
import org.springframework.messaging.simp.stomp.StompHeaders;

@Slf4j
public class TestGameListenerUser extends AbstractTestUser {
    public TestGameListenerUser(TestUserParams params) {
        super(params);

    }

    @Override
    protected void handleMessage(StompHeaders headers, ServerMessageDTO message) {
        var validator = params.getValidator();
        if (validator != null) {
            try {
                validator.validateHandleMessage(message);
            } catch (Exception | AssertionFailedError e) {
                log.error("Validation on handle message failed", e);
                if (session != null) {
                    getExceptionThrown().compareAndSet(null, e);
                }
            }
        }

        var payload = message.getPayload();
        var latches = params.getLatches();
        if (payload instanceof GameFinishedDTO dto) {
            countdownLatch(latches.gameLatch());
        }
    }

    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.LISTENER;
    }
}
