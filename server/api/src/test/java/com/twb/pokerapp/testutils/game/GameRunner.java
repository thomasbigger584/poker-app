package com.twb.pokerapp.testutils.game;

import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
import com.twb.pokerapp.testutils.game.player.impl.TestGameListenerUser;
import com.twb.pokerapp.testutils.game.player.impl.TestTexasHoldemPlayerUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.testutils.http.message.PlayersServerMessages;
import com.twb.pokerapp.testutils.keycloak.KeycloakClients;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class GameRunner {
    private final GameRunnerParams params;

    public PlayersServerMessages run(Map<String, TurnHandler> turnHandlers) throws Exception {
        var players = getPlayers(turnHandlers);
        return run(players);
    }

    private PlayersServerMessages run(List<AbstractTestUser> players) throws Exception {
        var listener = connectListener();
        connectPlayers(players);

        params.getLatches().roundLatch().await();

        disconnectPlayers(players);

        params.getLatches().gameLatch().await();

        listener.disconnect();

        throwExceptionIfOccurred(players);

        var messages = new PlayersServerMessages(listener, players);
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

    private void connectPlayers(List<AbstractTestUser> players) throws Exception {
        for (var player : players) {
            player.connect();
        }
    }

    private void disconnectPlayers(List<AbstractTestUser> players) {
        for (var player : players) {
            player.disconnect();
        }
    }

    private List<AbstractTestUser> getPlayers(Map<String, TurnHandler> playerToTurnHandler) {
        var players = new ArrayList<AbstractTestUser>();
        for (var playerTurn : playerToTurnHandler.entrySet()) {
            var username = playerTurn.getKey();
            var keycloak = params.getKeycloakClients().get(username);
            var userParams = TestUserParams.builder()
                    .table(params.getTable())
                    .username(username)
                    .latches(params.getLatches())
                    .keycloak(keycloak)
                    .turnHandler(playerTurn.getValue())
                    .validator(params.getValidator())
                    .build();
            players.add(new TestTexasHoldemPlayerUser(userParams));
        }
        return players;
    }

    private void throwExceptionIfOccurred(List<AbstractTestUser> players) {
        for (var player : players) {
            var exceptionThrown = player.getExceptionThrown();
            if (exceptionThrown.get() != null) {
                throw new RuntimeException("Test Failure for player: " + player, exceptionThrown.get());
            }
        }
    }
}
