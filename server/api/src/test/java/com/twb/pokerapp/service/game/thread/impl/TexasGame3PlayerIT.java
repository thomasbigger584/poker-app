package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.testutils.testcontainers.BaseTestContainersIT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

@Slf4j
class TexasGame3PlayerIT extends BaseTestContainersIT {
    static {
        useFixedScenario = true;
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/texas-holdem-3player-scenarios.csv", numLinesToSkip = 1)
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
        System.out.println("TexasGame3PlayerIT.test3PlayerScenariosFromCsv");
        System.out.println("scenario = " + scenario + ", user1Hand = " + user1Hand + ", user1Start = " + user1Start + ", user2Hand = " + user2Hand + ", user2Start = " + user2Start + ", user3Hand = " + user3Hand + ", user3Start = " + user3Start + ", community = " + community + ", preFlop = " + preFlop + ", flop = " + flop + ", turn = " + turn + ", river = " + river + ", expectedWinners = " + expectedWinners + ", totalPot = " + totalPot + ", user1Win = " + user1Win + ", user2Win = " + user2Win + ", user3Win = " + user3Win);
//        var params = GameRunnerParams.builder()
//                .keycloakClients(keycloakClients)
//                .numberOfRounds(1)
//                .latches(GameLatches.create())
//                .table(adminRestClient.createTable(3))
//                .validator(validator)
//                .buyInAmounts(List.of(user1Start, user2Start, user3Start))
//                .build();
//        validator = new TexasValidator(params, sqlClient);
//        runner = new GameRunner(params, sqlClient);
//
//        var user1TurnHandler = new FixedScenarioTurnHandler();
//        var user2TurnHandler = new FixedScenarioTurnHandler();
//        var user3TurnHandler = new FixedScenarioTurnHandler();
//        var turnHandlers = TurnHandler.of(user1TurnHandler, user2TurnHandler, user3TurnHandler);
//
//        var messages = runner.run(turnHandlers);
//
//        validator.validateEndOfRun(messages);
    }
}
