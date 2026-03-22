package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.game.GameRunner;
import com.twb.pokerapp.testutils.game.params.GameRunnerParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioParams;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioPlayer;
import com.twb.pokerapp.testutils.game.turn.impl.FixedScenarioTurnHandler;
import com.twb.pokerapp.testutils.testcontainers.BaseTestContainersIT;
import com.twb.pokerapp.testutils.validator.impl.TexasValidator;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
class TexasGame3PlayerIT extends BaseTestContainersIT {
    static {
        useFixedScenario = true;
    }

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

        var params = GameRunnerParams.builder()
                .keycloakClients(keycloakClients)
                .numberOfRounds(1)
                .latches(GameLatches.create())
                .table(adminRestClient.createTable(3))
                .validator(validator)
                .scenarioParams(scenarioParams)
                .build();
        validator = new TexasValidator(params, sqlClient);
        runner = new GameRunner(params, sqlClient);

//        var messages = runner.run();
//
//        validator.validateEndOfRun(messages);
    }
}
