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
//    @ParameterizedTest(name = "{0}") // Displays the 'Logic_Check' string as the test name
//    @CsvFileSource(resources = "/texas-holdem-3player-scenarios.csv", numLinesToSkip = 1)
//    void test3PlayerScenariosFromCsv(
//            String scenario,
//            String u1Hand,
//            String u2Hand,
//            String u3Hand,
//            String community,
//            String preFlop,
//            String flop,
//            String turn,
//            String river,
//            String expectedWinners,
//            int totalPot,
//            int u1Win,
//            int u2Win,
//            int u3Win
//    ) {
//        System.out.println("TexasGame3PlayerIT.test3PlayerScenariosFromCsv");
//        System.out.println("scenario = " + scenario + ", u1Hand = " + u1Hand + ", u2Hand = " + u2Hand + ", u3Hand = " + u3Hand + ", community = " + community + ", preFlop = " + preFlop + ", flop = " + flop + ", turn = " + turn + ", river = " + river + ", expectedWinners = " + expectedWinners + ", totalPot = " + totalPot + ", u1Win = " + u1Win + ", u2Win = " + u2Win + ", u3Win = " + u3Win);
//
//    }
//
//}
