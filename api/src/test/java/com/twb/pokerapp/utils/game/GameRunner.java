package com.twb.pokerapp.utils.game;

import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.player.TestUserParams;
import com.twb.pokerapp.utils.game.player.impl.TestGameListenerUser;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


@RequiredArgsConstructor
public class GameRunner {
    private static final int LATCH_TIMEOUT_IN_SECS = 100;
    private static final String LISTENER = "viewer";
    private static final int PLAYER_WAIT_MS = 1000;

    private final GameRunnerParams params;

    public Map<String, List<ServerMessageDTO>> run(GameLatches latches,
                                                   List<AbstractTestUser> players) throws Exception {
        AbstractTestUser listener = new TestGameListenerUser(TestUserParams.builder()
                .table(params.getTable()).username(LISTENER)
                .latches(latches).build(), params.getNumberOfRounds());
        listener.connect();
        Thread.sleep(PLAYER_WAIT_MS);

        for (AbstractTestUser player : players) {
            player.connect();
            Thread.sleep(PLAYER_WAIT_MS);
        }

        latches.roundLatch().await(LATCH_TIMEOUT_IN_SECS, TimeUnit.SECONDS);

        for (AbstractTestUser player : players) {
            player.disconnect();
            Thread.sleep(PLAYER_WAIT_MS);
        }

        latches.gameLatch().await(LATCH_TIMEOUT_IN_SECS, TimeUnit.SECONDS);

        listener.disconnect();

        for (AbstractTestUser player : players) {
            AtomicReference<Throwable> exceptionThrown = player.getExceptionThrown();
            if (exceptionThrown.get() != null) {
                throw new RuntimeException("Test Failure for player: " + player, exceptionThrown.get());
            }
        }
        Map<String, List<ServerMessageDTO>> receivedMessages = new HashMap<>();
        receivedMessages.put(LISTENER, listener.getReceivedMessages());
        for (AbstractTestUser player : players) {
            receivedMessages.put(player.getParams().getUsername(), player.getReceivedMessages());
        }
        return receivedMessages;
    }
}
