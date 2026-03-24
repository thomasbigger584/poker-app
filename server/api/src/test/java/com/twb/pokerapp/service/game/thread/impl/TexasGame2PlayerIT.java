package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.testutils.TestEnvironment;
import com.twb.pokerapp.testutils.TestScenario;
import com.twb.pokerapp.testutils.game.turn.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class TexasGame2PlayerIT {
    private final static TestEnvironment env = new TestEnvironment();

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @BeforeAll
    static void beforeAll() {
        env.start();
    }

    @AfterEach
    void afterEach() {
        env.afterEach();
    }

    @AfterAll
    static void afterAll() {
        env.close();
    }

    // *****************************************************************************************
    // Test Methods
    // *****************************************************************************************

    @Test
    void testGameWithoutPlayerActions() throws Throwable {
        // given
        var scenario = new TestScenario(env).setup(null, null);

        // when
        var messages = scenario.run();

        // then
        var validator = scenario.getValidator();
        validator.validateEndOfRunConnections(messages);
    }

    @Test
    void testGameWithDefaultActions() throws Throwable {
        // given
        var scenario = new TestScenario(env).setup(
                new FirstActionTurnHandler(),
                new FirstActionTurnHandler()
        );

        // when
        var messages = scenario.run();

        // then
        var validator = scenario.getValidator();
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithOptimisticActions() throws Throwable {
        // given
        var scenario = new TestScenario(env).setup(
                new OptimisticTurnHandler(),
                new OptimisticTurnHandler()
        );

        // when
        var messages = scenario.run();

        // then
        var validator = scenario.getValidator();
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithSingleOptimisticActions() throws Throwable {
        // given
        var scenario = new TestScenario(env).setup(
                new OptimisticTurnHandler(),
                new FirstActionTurnHandler()
        );

        // when
        var messages = scenario.run();

        // then
        var validator = scenario.getValidator();
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithBetAndRaiseActions() throws Throwable {
        // given
        var scenario = new TestScenario(env).setup(
                new OptimisticTurnHandler(),
                new AggresiveTurnHandler()
        );

        // when
        var messages = scenario.run();

        // then
        var validator = scenario.getValidator();
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithSingleFirstActionActions() throws Throwable {
        // given
        var scenario = new TestScenario(env).setup(
                new FirstActionTurnHandler(),
                new OptimisticTurnHandler()
        );

        // when
        var messages = scenario.run();

        // then
        var validator = scenario.getValidator();
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithAllInAction() throws Throwable {
        // given
        var scenario = new TestScenario(env).setup(
                new OptimisticTurnHandler(),
                new AllInTurnHandler()
        );

        // when
        var messages = scenario.run();

        // then
        var validator = scenario.getValidator();
        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithInvalidActions() throws Throwable {
        // given
        var scenario = new TestScenario(env).setup(
                new OptimisticTurnHandler(),
                new InvalidActionTurnHandler()
        );

        // when
        var messages = scenario.run();

        // then
        var validator = scenario.getValidator();
        validator.validateInvalidAction(messages);
        validator.validateEndOfRunConnections(messages);
    }
}
