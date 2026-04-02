package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.testutils.TestEnvironment;
import com.twb.pokerapp.testutils.TestScenario;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioPlayer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;

@Slf4j
class TexasGame3PlayerIT {
    private final static TestEnvironment env = new TestEnvironment();
    private static final int SPEED_MULTIPLIER = 2;
    private static final boolean FIXED_SCENARIO = true;

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

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(
            resources = "/texas-holdem-3player-scenarios.csv",
            numLinesToSkip = 1,
            nullValues = "None"
    )
    void test3PlayerScenariosFromCsv(
            String scenario,
            String user1Hand,
            double user1Start,
            String user2Hand,
            double user2Start,
            String user3Hand,
            double user3Start,
            String community,
            String preFlop,
            String flop,
            String turn,
            String river,
            String expectedWinners,
            double totalPot,
            double user1Win,
            double user2Win,
            double user3Win
    ) throws Exception {
        // given
        var players = List.of(
                ScenarioPlayer.create("user1", user1Hand, user1Start, preFlop, flop, turn, river, user1Win),
                ScenarioPlayer.create("user2", user2Hand, user2Start, preFlop, flop, turn, river, user2Win),
                ScenarioPlayer.create("user3", user3Hand, user3Start, preFlop, flop, turn, river, user3Win)
        );
        var params = ScenarioParams.builder()
                .scenario(scenario)
                .speedMultiplier(SPEED_MULTIPLIER)
                .totalRounds(1)
                .useFixedScenario(FIXED_SCENARIO)
                .scenarioPlayers(players)
                .communityCards(community)
                .build();
        var testScenario = new TestScenario(env).setup(params);

        // when
        var messages = testScenario.run();

        // then
        var validator = testScenario.getValidator();
        validator.validateEndOfRun(messages);
    }
}
