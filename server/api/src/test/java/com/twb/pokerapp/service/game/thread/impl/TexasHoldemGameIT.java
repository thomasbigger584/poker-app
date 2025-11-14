package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.utils.game.GameLatches;
import com.twb.pokerapp.utils.game.GameRunner;
import com.twb.pokerapp.utils.game.GameRunnerParams;
import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.player.TestUserParams;
import com.twb.pokerapp.utils.game.player.impl.TestTexasHoldemPlayerUser;
import com.twb.pokerapp.utils.game.turn.TurnHandler;
import com.twb.pokerapp.utils.game.turn.impl.DefaultTurnHandler;
import com.twb.pokerapp.utils.testcontainers.BaseTestContainersIT;
import com.twb.pokerapp.utils.validator.impl.TexasHoldemValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
class TexasHoldemGameIT extends BaseTestContainersIT {
    private static final String PLAYER_1 = "user1";
    private static final String PLAYER_2 = "user2";

    private GameRunnerParams params;
    private GameRunner runner;

    @Override
    protected void beforeEach() throws Throwable {
        this.validator = new TexasHoldemValidator(sqlClient);
        this.params = GameRunnerParams.builder()
                .keycloakClients(keycloakClients)
                .numberOfRounds(1)
                .latches(GameLatches.create())
                .table(getTexasHoldemTable())
                .validator(this.validator)
                .build();
        this.runner = new GameRunner(params);
    }

    @Test
    void testGameWithoutPlayerActions() throws Throwable {
        var turnHandlers = new HashMap<String, TurnHandler>();
        turnHandlers.put(PLAYER_1, null);
        turnHandlers.put(PLAYER_2, null);

        var messages = runner.run(getPlayers(turnHandlers))
                .getByNumberOfRounds(params.getNumberOfRounds());
        System.out.println("messages = " + messages);

        validator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithDefaultActions() throws Throwable {
        var turnHandlers = new HashMap<String, TurnHandler>();
        turnHandlers.put(PLAYER_1, new DefaultTurnHandler());
        turnHandlers.put(PLAYER_2, new DefaultTurnHandler());

        var messages = runner.run(getPlayers(turnHandlers))
                .getByNumberOfRounds(params.getNumberOfRounds());
        System.out.println("messages = " + messages);

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
