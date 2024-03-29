package com.twb.pokerapp.utils.game;

import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.player.AbstractTestUser.PlayerTurnHandler;
import com.twb.pokerapp.utils.game.player.TestUserParams;
import com.twb.pokerapp.utils.game.player.impl.TestGameListenerUser;
import com.twb.pokerapp.utils.game.player.impl.TestTexasHoldemPlayerUser;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TexasHoldemGameRunner {
    private static final int LATCH_TIMEOUT_IN_SECS = 100;
    private static final String LISTENER_USERNAME = "viewer";
    private static final String PLAYER_1_USERNAME = "thomas";
    private static final String PLAYER_2_USERNAME = "rory";
    private static final String PASSWORD = "password";
    private static final int PLAYER_WAIT_MS = 1000;
    private final GameRunnerParams params;

    public TexasHoldemGameRunner(GameRunnerParams params) {
        this.params = params;
    }

    public Map<String, List<ServerMessageDTO>> run() throws Exception {
        return run(null, null);
    }

    public Map<String, List<ServerMessageDTO>> run(PlayerTurnHandler handler1,
                                                   PlayerTurnHandler handler2) throws Exception {

        AbstractTestUser.CountdownLatches latches = AbstractTestUser.CountdownLatches.create();

        AbstractTestUser listener = new TestGameListenerUser(TestUserParams.builder()
                .table(params.getTable()).username(LISTENER_USERNAME).password(PASSWORD)
                .latches(latches).build(), params.getNumberOfRounds());
        listener.connect();
        Thread.sleep(PLAYER_WAIT_MS);

        List<AbstractTestUser> players = new ArrayList<>();

        players.add(new TestTexasHoldemPlayerUser(TestUserParams.builder()
                .table(params.getTable()).username(PLAYER_1_USERNAME).password(PASSWORD)
                .latches(latches).turnHandler(handler1).build()));
        players.add(new TestTexasHoldemPlayerUser(TestUserParams.builder()
                .table(params.getTable()).username(PLAYER_2_USERNAME).password(PASSWORD)
                .latches(latches).turnHandler(handler2).build()));

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
        receivedMessages.put(LISTENER_USERNAME, listener.getReceivedMessages());
        for (AbstractTestUser player : players) {
            receivedMessages.put(player.getParams().getUsername(), player.getReceivedMessages());
        }
        return receivedMessages;
    }
}
