package com.twb.pokerapp.utils.game;

import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.player.TestUserParams;
import com.twb.pokerapp.utils.game.player.impl.TestGameListenerUser;
import com.twb.pokerapp.utils.keycloak.KeycloakClients;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;

@RequiredArgsConstructor
public class GameRunner {
    private static final int LATCH_TIMEOUT_IN_SECS = 100;
    private static final int PLAYER_WAIT_MS = 1000;

    private final GameRunnerParams params;

    public Map<String, List<ServerMessageDTO>> run(List<AbstractTestUser> players) throws Exception {
        AbstractTestUser listener = connectListener();
        connectPlayers(players);

        params.getLatches().roundLatch()
                .await(LATCH_TIMEOUT_IN_SECS, TimeUnit.SECONDS);

        disconnectPlayers(players);

        params.getLatches().gameLatch()
                .await(LATCH_TIMEOUT_IN_SECS, TimeUnit.SECONDS);

        listener.disconnect();

        throwExceptionIfOccurred(players);

        return getReceivedMessages(players, listener);
    }

    private AbstractTestUser connectListener() throws Exception {
        Keycloak keycloak = params.getKeycloakClients().getViewerKeycloak();
        TestUserParams listenerParams = TestUserParams.builder()
                .table(params.getTable())
                .keycloak(keycloak)
                .latches(params.getLatches())
                .username(KeycloakClients.KEYCLOAK_VIEWER_USERNAME)
                .build();
        AbstractTestUser listener = new TestGameListenerUser(listenerParams, params.getNumberOfRounds());
        listener.connect();
        sleep(PLAYER_WAIT_MS);
        return listener;
    }

    private void connectPlayers(List<AbstractTestUser> players) throws Exception {
        for (AbstractTestUser player : players) {
            player.connect();
            sleep(PLAYER_WAIT_MS);
        }
    }

    private void disconnectPlayers(List<AbstractTestUser> players) throws Exception {
        for (AbstractTestUser player : players) {
            player.disconnect();
            sleep(PLAYER_WAIT_MS);
        }
    }

    private void throwExceptionIfOccurred(List<AbstractTestUser> players) {
        for (AbstractTestUser player : players) {
            AtomicReference<Throwable> exceptionThrown = player.getExceptionThrown();
            if (exceptionThrown.get() != null) {
                throw new RuntimeException("Test Failure for player: " + player, exceptionThrown.get());
            }
        }
    }

    private Map<String, List<ServerMessageDTO>> getReceivedMessages(List<AbstractTestUser> players,
                                                                    AbstractTestUser listener) {
        Map<String, List<ServerMessageDTO>> receivedMessages = new HashMap<>();
        receivedMessages.put(KeycloakClients.KEYCLOAK_VIEWER_USERNAME, listener.getReceivedMessages());
        for (AbstractTestUser player : players) {
            receivedMessages.put(player.getParams().getUsername(), player.getReceivedMessages());
        }
        return receivedMessages;
    }
}
