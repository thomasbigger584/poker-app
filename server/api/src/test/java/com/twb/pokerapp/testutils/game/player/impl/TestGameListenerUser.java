package com.twb.pokerapp.testutils.game.player.impl;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.proto.ServerMessageDTO;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
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
        var latches = params.getLatches();
        if (validator != null) {
            try {
                validator.validateHandleMessage(message);
            } catch (Exception | AssertionFailedError e) {
                log.error("Validation on handle message failed", e);
                if (session != null) {
                    getExceptionThrown().compareAndSet(null, e);
                    countdownLatch(latches.gameLatch());
                    return;
                }
            }
        }
        if (message.getPayloadCase() == ServerMessageDTO.PayloadCase.GAME_FINISHED) {
            countdownLatch(latches.gameLatch());
        }
    }

    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.LISTENER;
    }
}
