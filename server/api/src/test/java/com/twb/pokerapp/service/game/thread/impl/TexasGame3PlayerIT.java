package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.testutils.TestEnvironment;
import com.twb.pokerapp.testutils.TestScenario;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioPlayer;
import com.twb.pokerapp.testutils.game.turn.impl.FixedScenarioTurnHandler;
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

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @BeforeAll
    static void beforeAll() {
        env.start(true);
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
        var players = List.of(
                ScenarioPlayer.builder()
                        .username("user1")
                        .handCards(user1Hand)
                        .buyIn(user1Start)
                        .turnHandler(new FixedScenarioTurnHandler())
                        .winAmount(user1Win)
                        .build(),
                ScenarioPlayer.builder()
                        .username("user2")
                        .handCards(user2Hand)
                        .buyIn(user2Start)
                        .turnHandler(new FixedScenarioTurnHandler())
                        .winAmount(user2Win)
                        .build(),
                ScenarioPlayer.builder()
                        .username("user3")
                        .handCards(user3Hand)
                        .buyIn(user3Start)
                        .turnHandler(new FixedScenarioTurnHandler())
                        .winAmount(user3Win)
                        .build()
                );
        var scenarioParams = ScenarioParams.builder()
                .useFixedScenario(true)
                .scenarioPlayers(players)
                .communityCards(community)
                .build();

//        var messages = new TestScenario(env)
//                .setupScenario(scenarioParams)
//                .run();

//        validator.validateEndOfRun(messages);
    }
}
