package com.twb.pokerapp.testutils.game;

import com.twb.pokerapp.testutils.game.params.GameRunnerParams;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
import com.twb.pokerapp.testutils.game.player.impl.TestGameListenerUser;
import com.twb.pokerapp.testutils.game.player.impl.TestTexasHoldemPlayerUser;
import com.twb.pokerapp.testutils.http.message.PlayersServerMessages;
import com.twb.pokerapp.testutils.keycloak.KeycloakClients;
import com.twb.pokerapp.testutils.sql.SqlClient;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public class GameRunner {
    private final GameRunnerParams params;

    public PlayersServerMessages run() throws Exception {
        var listenerUser = connectListener();

        var playerUsers = getPlayerUsers();
        connectPlayers(playerUsers);

        params.getLatches().roundLatch().await();

        disconnectPlayers(playerUsers);

        params.getLatches().gameLatch().await();

        listenerUser.disconnect();

        throwExceptionIfOccurred(playerUsers);

        var messages = new PlayersServerMessages(listenerUser, playerUsers);
        return messages.getByNumberOfRounds(params.getNumberOfRounds());
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    private AbstractTestUser connectListener() throws Exception {
        var keycloak = params.getKeycloakClients().getViewerKeycloak();
        var listenerParams = TestUserParams.builder()
                .table(params.getTable())
                .keycloak(keycloak)
                .latches(params.getLatches())
                .username(KeycloakClients.VIEWER_USERNAME)
                .validator(params.getValidator())
                .build();
        var listener = new TestGameListenerUser(listenerParams, params.getNumberOfRounds());
        listener.connect();
        return listener;
    }

    private void connectPlayers(List<AbstractTestUser> playerUsers) throws Exception {
        var scenarioPlayers = params.getScenarioParams().getScenarioPlayers();
        for (var clientPlayer : playerUsers) {
            var scenarioPlayerOpt = scenarioPlayers.stream()
                    .filter(scenarioPlayer ->
                            scenarioPlayer.getUsername().equals(clientPlayer.getParams().getUsername()))
                    .findFirst();
            assertTrue(scenarioPlayerOpt.isPresent());
            var scenarioPlayer = scenarioPlayerOpt.get();

            clientPlayer.connect(scenarioPlayer.getBuyIn());
        }
    }

    private void disconnectPlayers(List<AbstractTestUser> players) {
        for (var player : players) {
            player.disconnect();
        }
    }

    private List<AbstractTestUser> getPlayerUsers() {
        var scenarioPlayers = params.getScenarioParams().getScenarioPlayers();
        var playerUsers = new ArrayList<AbstractTestUser>();
        for (var scenarioPlayer : scenarioPlayers) {
            var username = scenarioPlayer.getUsername();
            var keycloak = params.getKeycloakClients().get(username);
            var userParams = TestUserParams.builder()
                    .table(params.getTable()).username(username)
                    .latches(params.getLatches())
                    .keycloak(keycloak)
                    .turnHandler(scenarioPlayer.getTurnHandler())
                    .validator(params.getValidator())
                    .build();
            playerUsers.add(new TestTexasHoldemPlayerUser(userParams));
        }
        return playerUsers;
    }

    private void throwExceptionIfOccurred(List<AbstractTestUser> players) {
        for (var player : players) {
            var exceptionThrown = player.getExceptionThrown().get();
            if (exceptionThrown != null) {
                throw new RuntimeException("Test Failure for player: " + player.getUsername(), exceptionThrown);
            }
        }
    }
}
