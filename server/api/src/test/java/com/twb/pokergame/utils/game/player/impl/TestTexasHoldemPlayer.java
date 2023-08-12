package com.twb.pokergame.utils.game.player.impl;

import com.twb.pokergame.utils.game.player.AbstractTestPlayer;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.web.websocket.message.server.payload.GameFinishedDTO;
import com.twb.pokergame.web.websocket.message.server.payload.RoundFinishedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TestTexasHoldemPlayer extends AbstractTestPlayer {
    private final static Logger logger = LoggerFactory.getLogger(TestTexasHoldemPlayer.class);
    private final AtomicInteger roundCountAtomicInteger = new AtomicInteger(0);
    private final int numOfRounds;

    public TestTexasHoldemPlayer(UUID tableId, CountdownLatches latches,
                                 String username, String password, int numOfRounds) {
        super(tableId, latches, username, password);
        this.numOfRounds = numOfRounds;
    }

    @Override
    protected void handleMessage(StompHeaders headers, ServerMessageDTO message) {
        Object payload = message.getPayload();

        // stopping game after a certain number of rounds
        if (payload instanceof RoundFinishedDTO dto) {
            int thisRoundCount = roundCountAtomicInteger.incrementAndGet();
            if (thisRoundCount == numOfRounds) {
                latches.roundLatch().countDown();
            }
        // stopping tests when all players disconnect to cover full lifecycle
        } else if (payload instanceof GameFinishedDTO dto) {
            System.out.println("GAME FINISHED PAYLOAD RECIEVED........");

            latches.gameLatch().countDown();
        }
    }
}
