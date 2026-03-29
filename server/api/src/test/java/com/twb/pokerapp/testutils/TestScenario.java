package com.twb.pokerapp.testutils;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.game.GameRunner;
import com.twb.pokerapp.testutils.game.params.GameRunnerParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioPlayer;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.testutils.http.message.PlayersServerMessages;
import com.twb.pokerapp.testutils.validator.Validator;
import com.twb.pokerapp.testutils.validator.impl.TexasValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TestScenario {
    private static final Double DEFAULT_BUY_IN_AMOUNT = 5_000d;
    private final TestEnvironment env;

    @Getter
    private Validator validator;
    private GameRunner runner;

    // *****************************************************************************************
    // Public Methods
    // *****************************************************************************************

    public TestScenario setup(ScenarioParams params) throws Exception {
        setupDatabase(params);

        var sqlClient = env.getSqlClient();
        var keycloakClients = env.getKeycloakClients();
        
        var playerCount = params.getScenarioPlayers().size();
        var adminRestClient = env.getAdminRestClient();
        var table = adminRestClient.createTable(playerCount);

        this.validator = new TexasValidator(params, sqlClient);

        var gameRunnerParams = GameRunnerParams.builder()
                .keycloakClients(keycloakClients)
                .numberOfRounds(1)
                .latches(GameLatches.create())
                .table(table)
                .validator(validator)
                .scenarioParams(params)
                .build();

        this.runner = new GameRunner(gameRunnerParams);
        return this;
    }

    public TestScenario setup(TurnHandler... turnHandlers) throws Exception {
        var scenarioPlayers = new ArrayList<ScenarioPlayer>();
        for (var index = 0; index < turnHandlers.length; index++) {
            scenarioPlayers.add(
                    ScenarioPlayer.builder()
                        .username("user" + (index + 1))
                        .buyIn(DEFAULT_BUY_IN_AMOUNT)
                        .turnHandler(turnHandlers[index])
                        .build()
            );
        }
        var scenarioParams = ScenarioParams.builder()
                .useFixedScenario(false)
                .scenarioPlayers(scenarioPlayers)
                .build();
        return setup(scenarioParams);
    }

    public PlayersServerMessages run() throws Exception {
        if (this.runner == null) {
            throw new IllegalStateException("Scenario not setup");
        }
        var messages = this.runner.run();
        waitForSessionsToDisconnect();
        return messages;
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private void setupDatabase(ScenarioParams params) {
        var sqlClient = env.getSqlClient();
        if (params.isUseFixedScenario()) {
            sqlClient.insertFixedScenario(params);
        }
        sqlClient.updateUsersTotalFunds(params);
    }

    private void waitForSessionsToDisconnect() {
        var timeout = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < timeout) {
            var sessions = env.getSqlClient().getPlayerSessions();
            if (sessions.isEmpty()|| isAllDisconnected(sessions)) {
                return;
            }
            try {
                //noinspection BusyWait
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to sleep waiting for sessions to disconnect", e);
            }
        }
    }

    private boolean isAllDisconnected(List<PlayerSession> sessions) {
        return sessions.stream().allMatch(playerSession -> SessionState.CONNECTED != playerSession.getSessionState());
    }
}
