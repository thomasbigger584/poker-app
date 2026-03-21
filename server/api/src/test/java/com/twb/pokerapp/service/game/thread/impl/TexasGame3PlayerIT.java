//package com.twb.pokerapp.service.game.thread.impl;
//
//import com.twb.pokerapp.testutils.game.GameLatches;
//import com.twb.pokerapp.testutils.game.GameRunner;
//import com.twb.pokerapp.testutils.game.GameRunnerParams;
//import com.twb.pokerapp.testutils.game.turn.impl.*;
//import com.twb.pokerapp.testutils.testcontainers.BaseTestContainersIT;
//import com.twb.pokerapp.testutils.validator.impl.TexasValidator;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvFileSource;
//
//@Slf4j
//class TexasGame3PlayerIT extends BaseTestContainersIT {
//    private static final int PLAYER_COUNT = 3;
//    private static final Double BUY_IN_AMOUNT = 5_000d;
//
//    @Override
//    protected void beforeEach() throws Exception {
//        var params = GameRunnerParams.builder()
//                .keycloakClients(keycloakClients)
//                .numberOfRounds(1)
//                .latches(GameLatches.create())
//                .table(adminRestClient.createTable(PLAYER_COUNT))
//                .validator(validator)
//                .buyinAmount(BUY_IN_AMOUNT)
//                .build();
//        validator = new TexasValidator(params, sqlClient);
//        runner = new GameRunner(params);
//        sqlClient.updateUsersTotalFunds(params.getBuyinAmount());
//    }
//
//    @ParameterizedTest(name = "{0}") // Displays the 'Scenario' string as the test name
//    @CsvFileSource(resources = "/texas-holdem-3player-scenarios.csv", numLinesToSkip = 1)
//    void test3PlayerScenariosFromCsv(
//            String scenario,
//            String user1Hand,
//            int user1Start,
//            String user2Hand,
//            int user2Start,
//            String user3Hand,
//            int user3Start,
//            String community,
//            String preFlop,
//            String flop,
//            String turn,
//            String river,
//            String expectedWinners,
//            int totalPot,
//            int user1Win,
//            int user2Win,
//            int user3Win
//    ) {
//        System.out.println("TexasGame3PlayerIT.test3PlayerScenariosFromCsv");
//        System.out.println("scenario = " + scenario + ", user1Hand = " + user1Hand + ", user1Start = " + user1Start + ", user2Hand = " + user2Hand + ", user2Start = " + user2Start + ", user3Hand = " + user3Hand + ", user3Start = " + user3Start + ", community = " + community + ", preFlop = " + preFlop + ", flop = " + flop + ", turn = " + turn + ", river = " + river + ", expectedWinners = " + expectedWinners + ", totalPot = " + totalPot + ", user1Win = " + user1Win + ", user2Win = " + user2Win + ", user3Win = " + user3Win);
//
//    }
//
//}
