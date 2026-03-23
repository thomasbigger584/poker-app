package com.twb.pokerapp.testutils;

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

@RequiredArgsConstructor
public class TestScenario {
    private static final Double DEFAULT_BUY_IN_AMOUNT = 5_000d;
    private final TestEnvironment env;

    @Getter
    private Validator validator;
    private GameRunner runner;

    public TestScenario setupScenario(ScenarioParams params) throws Exception {
        setupDatabase(params);

        var sqlClient = env.getSqlClient();
        var keycloakClients = env.getKeycloakClients();
        
        var playerCount = params.getScenarioPlayers().size();
        var adminRestClient = env.getAdminRestClient();
        var table = adminRestClient.createTable(playerCount);

        this.validator = new TexasValidator(sqlClient);

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

    public TestScenario setupScenario(TurnHandler... turnHandlers) throws Exception {
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
        return setupScenario(scenarioParams);
    }

    public PlayersServerMessages run() throws Exception {
        return this.runner.run();
    }

    private void setupDatabase(ScenarioParams params) {
        var sqlClient = env.getSqlClient();
        if (params.isUseFixedScenario()) {
            sqlClient.insertFixedScenario(params);
        }
        sqlClient.updateUsersTotalFunds(params);
    }
}
