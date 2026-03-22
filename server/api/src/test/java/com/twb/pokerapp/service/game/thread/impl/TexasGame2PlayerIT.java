package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.game.GameRunner;
import com.twb.pokerapp.testutils.game.params.GameRunnerParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioPlayer;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.*;
import com.twb.pokerapp.testutils.testcontainers.BaseTestContainersIT;
import com.twb.pokerapp.testutils.validator.impl.TexasValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
class TexasGame2PlayerIT extends BaseTestContainersIT {
    private static final int PLAYER_COUNT = 2;
    private static final Double BUY_IN_AMOUNT = 5_000d;

    @Test
    void testGameWithoutPlayerActions() throws Throwable {
        // given
        setupScenario(null, null);

        // when
        var messages = runner.run();

        // then
        validator.validateEndOfRunConnections(messages);
    }

    @Test
    void testGameWithDefaultActions() throws Throwable {
        // given
        setupScenario(
            new FirstActionTurnHandler(),
            new FirstActionTurnHandler()
        );

        // when
        var messages = runner.run();

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithOptimisticActions() throws Throwable {
        // given
        setupScenario(
            new OptimisticTurnHandler(),
            new OptimisticTurnHandler()
        );

        // when
        var messages = runner.run();

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithSingleOptimisticActions() throws Throwable {
        // given
        setupScenario(
            new OptimisticTurnHandler(),
            new FirstActionTurnHandler()
        );

        // when
        var messages = runner.run();

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithBetAndRaiseActions() throws Throwable {
        // given
        setupScenario(
            new OptimisticTurnHandler(),
            new AggresiveTurnHandler()
        );

        // when
        var messages = runner.run();

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithSingleFirstActionActions() throws Throwable {
        // given
        setupScenario(
            new FirstActionTurnHandler(),
            new OptimisticTurnHandler()
        );

        // when
        var messages = runner.run();

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithAllInAction() throws Throwable {
        // given
        setupScenario(
            new OptimisticTurnHandler(),
            new AllInTurnHandler()
        );

        // when
        var messages = runner.run();

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithInvalidActions() throws Throwable {
        // given
        setupScenario(
            new OptimisticTurnHandler(),
            new InvalidActionTurnHandler()
        );

        // when
        var messages = runner.run();

        // then
        validator.validateInvalidAction(messages);
        validator.validateEndOfRunConnections(messages);
    }

    private void setupScenario(TurnHandler... turnHandlers) throws Exception {
        assert turnHandlers.length == PLAYER_COUNT;
        var players = List.of(
                ScenarioPlayer.builder()
                        .username("user1")
                        .buyIn(BUY_IN_AMOUNT)
                        .turnHandler(turnHandlers[0])
                        .build(),
                ScenarioPlayer.builder()
                        .username("user2")
                        .buyIn(BUY_IN_AMOUNT)
                        .turnHandler(turnHandlers[1])
                        .build()
        );
        var scenarioParams = ScenarioParams.builder()
                .useFixedScenario(false)
                .scenarioPlayers(players)
                .build();
        var params = GameRunnerParams.builder()
                .keycloakClients(keycloakClients)
                .numberOfRounds(1)
                .latches(GameLatches.create())
                .table(adminRestClient.createTable(PLAYER_COUNT))
                .validator(validator)
                .scenarioParams(scenarioParams)
                .build();
        validator = new TexasValidator(params, sqlClient);
        runner = new GameRunner(params, sqlClient);
    }
}
