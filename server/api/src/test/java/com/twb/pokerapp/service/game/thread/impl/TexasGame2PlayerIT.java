package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.testutils.TestEnvironment;
import com.twb.pokerapp.testutils.TestScenario;
import com.twb.pokerapp.testutils.game.turn.impl.InvalidActionTurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.OptimisticTurnHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

@Slf4j
class TexasGame2PlayerIT {
    private final static TestEnvironment env = new TestEnvironment();
    private static final boolean FIXED_SCENARIO = false;

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @BeforeAll
    static void beforeAll() {
        env.start(FIXED_SCENARIO);
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
    @Disabled("This sends check around on timeout so takes a while - can run this test manually for validation")
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
