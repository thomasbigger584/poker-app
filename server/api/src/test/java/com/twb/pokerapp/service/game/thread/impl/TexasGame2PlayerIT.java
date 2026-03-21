package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.game.GameRunner;
import com.twb.pokerapp.testutils.game.GameRunnerParams;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.*;
import com.twb.pokerapp.testutils.testcontainers.BaseTestContainersIT;
import com.twb.pokerapp.testutils.validator.impl.TexasValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class TexasGame2PlayerIT extends BaseTestContainersIT {
    private static final int PLAYER_COUNT = 2;
    private static final Double BUY_IN_AMOUNT = 5_000d;

    @Override
    protected void beforeEach() throws Exception {
        var params = GameRunnerParams.builder()
                .keycloakClients(keycloakClients)
                .numberOfRounds(1)
                .latches(GameLatches.create())
                .table(adminRestClient.createTable(PLAYER_COUNT))
                .validator(validator)
                .buyinAmount(BUY_IN_AMOUNT)
                .build();
        validator = new TexasValidator(params, sqlClient);
        runner = new GameRunner(params);
        sqlClient.updateUsersTotalFunds(params.getBuyinAmount());
    }

    @Test
    void testGameWithoutPlayerActions() throws Throwable {
        // given
        var turnHandlers = TurnHandler.of(null, null);

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRunConnections(messages);
    }

    @Test
    void testGameWithDefaultActions() throws Throwable {
        // given
        var turnHandlers = TurnHandler.of(
                new FirstActionTurnHandler(),
                new FirstActionTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithOptimisticActions() throws Throwable {
        // given
        var turnHandlers = TurnHandler.of(
                new OptimisticTurnHandler(),
                new OptimisticTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithSingleOptimisticActions() throws Throwable {
        // given
        var turnHandlers = TurnHandler.of(
                new OptimisticTurnHandler(),
                new FirstActionTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithBetAndRaiseActions() throws Throwable {
        // given
        var turnHandlers = TurnHandler.of(
                new OptimisticTurnHandler(),
                new AggresiveTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithSingleFirstActionActions() throws Throwable {
        // given
        var turnHandlers = TurnHandler.of(
                new FirstActionTurnHandler(),
                new OptimisticTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithAllInAction() throws Throwable {
        // given
        var turnHandlers = TurnHandler.of(
                new OptimisticTurnHandler(),
                new AllInTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithInvalidActions() throws Throwable {
        // given
        var turnHandlers = TurnHandler.of(
                new OptimisticTurnHandler(),
                new InvalidActionTurnHandler()
        );

        // when
        var messages = runner.run(turnHandlers);

        // then
        validator.validateInvalidAction(messages);
        validator.validateEndOfRunConnections(messages);
    }
}
