package com.twb.pokerapp.utils.game;

import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.player.TestUserParams;
import com.twb.pokerapp.utils.game.player.impl.TestGameListenerUser;
import com.twb.pokerapp.utils.http.message.PlayersServerMessages;
import com.twb.pokerapp.utils.keycloak.KeycloakClients;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class GameRunner {
    private final GameRunnerParams params;

    public PlayersServerMessages run(List<AbstractTestUser> players) throws Exception {
        AbstractTestUser listener = connectListener();
        connectPlayers(players);

        params.getLatches().roundLatch().await();

        disconnectPlayers(players);

        params.getLatches().gameLatch().await();

        listener.disconnect();

        throwExceptionIfOccurred(players);

        return new PlayersServerMessages(listener, players);
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    private AbstractTestUser connectListener() throws Exception {
        Keycloak keycloak = params.getKeycloakClients().getViewerKeycloak();
        TestUserParams listenerParams = TestUserParams.builder()
                .table(params.getTable())
                .keycloak(keycloak)
                .latches(params.getLatches())
                .username(KeycloakClients.VIEWER_USERNAME)
                .build();
        AbstractTestUser listener = new TestGameListenerUser(listenerParams, params.getNumberOfRounds());
        listener.connect();
        return listener;
    }

    private void connectPlayers(List<AbstractTestUser> players) throws Exception {
        for (AbstractTestUser player : players) {
            player.connect();
        }
    }

    private void disconnectPlayers(List<AbstractTestUser> players) {
        for (AbstractTestUser player : players) {
            player.disconnect();
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
}
