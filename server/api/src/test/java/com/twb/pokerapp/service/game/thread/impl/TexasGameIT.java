package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.game.GameRunner;
import com.twb.pokerapp.testutils.game.GameRunnerParams;
import com.twb.pokerapp.testutils.game.player.AbstractTestUser;
import com.twb.pokerapp.testutils.game.player.TestUserParams;
import com.twb.pokerapp.testutils.game.player.impl.TestTexasHoldemPlayerUser;
import com.twb.pokerapp.testutils.game.turn.TurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.FirstActionTurnHandler;
import com.twb.pokerapp.testutils.game.turn.impl.OptimisticTurnHandler;
import com.twb.pokerapp.testutils.testcontainers.BaseTestContainersIT;
import com.twb.pokerapp.testutils.validator.impl.TexasValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
class TexasGameIT extends BaseTestContainersIT {
    private static final String PLAYER_1 = "user1";
    private static final String PLAYER_2 = "user2";

    private GameRunnerParams params;
    private GameRunner runner;

    @Override
    protected void beforeEach() throws Throwable {
        this.params = GameRunnerParams.builder()
                .keycloakClients(keycloakClients)
                .numberOfRounds(1)
                .latches(GameLatches.create())
                .table(getTexasHoldemTable())
                .validator(validator)
                .build();
        this.validator = new TexasValidator(params, sqlClient);
        this.runner = new GameRunner(params);
    }

    @Test
    void testGameWithoutPlayerActions() throws Throwable {
        var turnHandlers = new HashMap<String, TurnHandler>();
        turnHandlers.put(PLAYER_1, null);
        turnHandlers.put(PLAYER_2, null);

        var messages = runner.run(getPlayers(turnHandlers));
        System.out.println("messages = " + messages);

        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithDefaultActions() throws Throwable {
        var turnHandlers = new HashMap<String, TurnHandler>();
        turnHandlers.put(PLAYER_1, new FirstActionTurnHandler());
        turnHandlers.put(PLAYER_2, new FirstActionTurnHandler());

        var messages = runner.run(getPlayers(turnHandlers));
        System.out.println("messages = " + messages);

        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithOptimisticActions() throws Throwable {
        var turnHandlers = new HashMap<String, TurnHandler>();
        turnHandlers.put(PLAYER_1, new OptimisticTurnHandler());
        turnHandlers.put(PLAYER_2, new OptimisticTurnHandler());

        var messages = runner.run(getPlayers(turnHandlers));

        validator.validateEndOfRun(messages);
    }


    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private PokerTable getTexasHoldemTable() {
        return sqlClient.getPokerTables()
                .stream()
                .filter(pokerTable -> pokerTable.getGameType() == GameType.TEXAS_HOLDEM)
                .findFirst()
                .orElseThrow();
    }

    private List<AbstractTestUser> getPlayers(Map<String, TurnHandler> playerToTurnHandler) {
        var players = new ArrayList<AbstractTestUser>();
        for (var playerTurn : playerToTurnHandler.entrySet()) {
            var username = playerTurn.getKey();
            var userParams = TestUserParams.builder()
                    .table(params.getTable())
                    .username(username)
                    .latches(params.getLatches())
                    .keycloak(keycloakClients.get(username))
                    .turnHandler(playerTurn.getValue())
                    .validator(validator)
                    .build();
            players.add(new TestTexasHoldemPlayerUser(userParams));
        }
        return players;
    }
}
