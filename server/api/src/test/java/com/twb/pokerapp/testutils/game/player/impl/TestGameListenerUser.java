package com.twb.pokerapp.testutils.game.player.impl;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.GameFinishedDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.RoundFinishedDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TestGameListenerUser extends AbstractTestUser {
    private final AtomicInteger roundCountAtomicInteger = new AtomicInteger(0);
    private final int numOfRounds;

    public TestGameListenerUser(TestUserParams params, int numOfRounds) {
        super(params);
        this.numOfRounds = numOfRounds;
    }

    @Override
    protected void handleMessage(StompHeaders headers, ServerMessageDTO message) {
        var validator = params.getValidator();
        if (validator != null) {
            validator.validateHandleMessage(message);
        }

        var payload = message.getPayload();

        // stopping game after a certain number of rounds
        if (payload instanceof RoundFinishedDTO dto) {
            var thisRoundCount = roundCountAtomicInteger.incrementAndGet();
            if (thisRoundCount == numOfRounds) {
                params.getLatches().roundLatch().countDown();
            }
            // stopping tests when all players disconnect to cover full lifecycle
        } else if (payload instanceof GameFinishedDTO dto) {
            params.getLatches().gameLatch().countDown();
        }
    }

    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.LISTENER;
    }
}
