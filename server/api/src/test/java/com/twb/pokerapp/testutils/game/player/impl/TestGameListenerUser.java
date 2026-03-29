package com.twb.pokerapp.testutils.game.player.impl;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.GameFinishedDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.RoundFinishedDTO;
import lombok.extern.slf4j.Slf4j;
import org.opentest4j.AssertionFailedError;
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
            try {
                validator.validateHandleMessage(message);
            } catch (Exception | AssertionFailedError e) {
                log.error("Validation on handle message failed", e);
                getExceptionThrown().compareAndSet(null, e);
            }
        }

        var payload = message.getPayload();
        var latches = params.getLatches();

        // stopping game after a certain number of rounds
        if (payload instanceof RoundFinishedDTO dto) {
            var thisRoundCount = roundCountAtomicInteger.incrementAndGet();
            if (thisRoundCount == numOfRounds) {
                latches.roundLatch().countDown();
            }
            // stopping tests when all players disconnect to cover full lifecycle
        } else if (payload instanceof GameFinishedDTO dto) {
            countdownLatch(latches.gameLatch());
        }
    }

    @Override
    protected ConnectionType getConnectionType() {
        return ConnectionType.LISTENER;
    }
}
