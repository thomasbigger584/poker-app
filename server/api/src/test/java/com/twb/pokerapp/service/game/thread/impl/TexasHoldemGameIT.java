package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.dto.pokertable.TableDTO;
import com.twb.pokerapp.exception.NotFoundException;
import com.twb.pokerapp.utils.game.GameLatches;
import com.twb.pokerapp.utils.game.GameRunner;
import com.twb.pokerapp.utils.game.GameRunnerParams;
import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.utils.game.player.TestUserParams;
import com.twb.pokerapp.utils.game.player.impl.TestTexasHoldemPlayerUser;
import com.twb.pokerapp.utils.game.turn.TurnHandler;
import com.twb.pokerapp.utils.game.turn.impl.DefaultTurnHandler;
import com.twb.pokerapp.utils.http.RestClient;
import com.twb.pokerapp.utils.http.RestClient.ApiHttpResponse;
import com.twb.pokerapp.utils.http.message.PlayersServerMessages;
import com.twb.pokerapp.utils.sql.validator.impl.TexasHoldemDbValidator;
import com.twb.pokerapp.utils.testcontainers.BaseTestContainersIT;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TexasHoldemGameIT extends BaseTestContainersIT {
    private static final Logger logger = LoggerFactory.getLogger(TexasHoldemGameIT.class);
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
                .build();
        this.runner = new GameRunner(params);
        this.dbValidator = new TexasHoldemDbValidator(sqlClient);
    }

    @Test
    void testGameWithoutPlayerActions() throws Throwable {
        Map<String, TurnHandler> turnHandlers = new HashMap<>();
        turnHandlers.put(PLAYER_1, null);
        turnHandlers.put(PLAYER_2, null);

        PlayersServerMessages messages = runner.run(getPlayers(turnHandlers))
                .getByNumberOfRounds(params.getNumberOfRounds());
        System.out.println("messages = " + messages);

        dbValidator.validateEndOfRun(messages);
    }

    @Test
    void testGameWithDefaultActions() throws Throwable {
        Map<String, TurnHandler> turnHandlers = new HashMap<>();
        turnHandlers.put(PLAYER_1, new DefaultTurnHandler());
        turnHandlers.put(PLAYER_2, new DefaultTurnHandler());

        PlayersServerMessages messages = runner.run(getPlayers(turnHandlers))
                .getByNumberOfRounds(params.getNumberOfRounds());
        System.out.println("messages = " + messages);

        dbValidator.validateEndOfRun(messages);
    }


    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private TableDTO getTexasHoldemTable() throws Exception {
        Keycloak keycloak = keycloakClients.getAdminKeycloak();
        RestClient client = RestClient.getInstance(keycloak);
        ApiHttpResponse<TableDTO[]> tablesResponse = client.get(TableDTO[].class, "/poker-table");
        assertEquals(HttpStatus.OK.value(), tablesResponse.httpResponse().statusCode());
        TableDTO[] tables = tablesResponse.resultBody();

        for (TableDTO tableDTO : tables) {
            if (tableDTO.getGameType() == GameType.TEXAS_HOLDEM) {
                return tableDTO;
            }
        }
        throw new NotFoundException("Failed to find a Texas Holdem Table");
    }

    private List<AbstractTestUser> getPlayers(Map<String, TurnHandler> playerToTurnHandler) {
        List<AbstractTestUser> players = new ArrayList<>();
        for (Map.Entry<String, TurnHandler> playerTurn : playerToTurnHandler.entrySet()) {
            String username = playerTurn.getKey();
            TestUserParams userParams = TestUserParams.builder()
                    .table(params.getTable())
                    .username(username)
                    .latches(params.getLatches())
                    .keycloak(keycloakClients.get(username))
                    .turnHandler(playerTurn.getValue())
                    .build();
            players.add(new TestTexasHoldemPlayerUser(userParams));
        }
        return players;
    }
}
