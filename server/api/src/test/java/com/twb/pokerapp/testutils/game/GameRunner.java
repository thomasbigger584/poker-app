package com.twb.pokerapp.testutils.game;

import com.twb.pokerapp.testutils.game.params.GameRunnerParams;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
import com.twb.pokerapp.testutils.game.player.impl.TestGameListenerUser;
import com.twb.pokerapp.testutils.game.player.impl.TestTexasHoldemPlayerUser;
import com.twb.pokerapp.testutils.http.message.PlayersServerMessages;
import com.twb.pokerapp.testutils.keycloak.KeycloakClients;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public class GameRunner {
    private static final int CONNECTION_STAGGER_MS = 1000;
    private final GameRunnerParams params;

    private AbstractTestUser listenerUser;
    private List<AbstractTestUser> playerUsers;

    // ***************************************************************
    // Public Methods
    // ***************************************************************

    public PlayersServerMessages run() throws Exception {
        this.listenerUser = connectListener();

        this.playerUsers = getPlayerUsers();
        connectPlayers();

        params.getLatches().gameLatch().await();

        disconnectPlayers();
        listenerUser.disconnect();

        throwExceptionIfOccurred();

        return new PlayersServerMessages(listenerUser, playerUsers);
    }

    public void stop() {
        if (listenerUser != null) {
            listenerUser.stop();
        }
        for (var player : playerUsers) {
            player.stop();
        }
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
        var listener = new TestGameListenerUser(listenerParams);
        listener.connect();
        return listener;
    }

    private void connectPlayers() throws Exception {
        var scenarioPlayers = params.getScenarioParams().getScenarioPlayers();
        for (var clientPlayer : playerUsers) {
            Thread.sleep(CONNECTION_STAGGER_MS);
            var scenarioPlayerOpt = scenarioPlayers.stream()
                    .filter(scenarioPlayer ->
                            scenarioPlayer.getUsername().equals(clientPlayer.getParams().getUsername()))
                    .findFirst();
            assertTrue(scenarioPlayerOpt.isPresent());
            var scenarioPlayer = scenarioPlayerOpt.get();

            clientPlayer.connect(scenarioPlayer.getBuyIn());
        }
    }

    private void disconnectPlayers() {
        for (var player : playerUsers) {
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

    private void throwExceptionIfOccurred() {
        var scenario = params.getScenarioParams().getScenario();
        var listenerThrowable = listenerUser.getExceptionThrown().get();
        if (listenerThrowable != null) {
            throw new RuntimeException(getExceptionMessage(listenerUser, scenario, listenerThrowable), listenerThrowable);
        }
        for (var player : playerUsers) {
            var playerException = player.getExceptionThrown().get();
            if (playerException != null) {
                throw new RuntimeException(getExceptionMessage(player, scenario, playerException), playerException);
            }
        }
    }

    private @NonNull String getExceptionMessage(AbstractTestUser player, String scenario, Throwable throwable) {
        var username = player.getUsername();
        var message = throwable.getMessage();
        if (StringUtils.isBlank(scenario)) {
            return "Failure for user: " + username + ": " + message;
        }
        return "Failure for user: " + username + " in scenario: " + scenario;
    }
}
