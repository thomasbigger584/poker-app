package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.dto.pokertable.TableDTO;
import com.twb.pokergame.exception.NotFoundException;
import com.twb.pokergame.utils.game.player.AbstractTestUser;
import com.twb.pokergame.utils.game.player.AbstractTestUser.CountdownLatches;
import com.twb.pokergame.utils.game.player.impl.TestGameListenerUser;
import com.twb.pokergame.utils.game.player.impl.TestTexasHoldemPlayerUser;
import com.twb.pokergame.utils.testcontainers.BaseTestContainersIT;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessageType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TexasHoldemGameIT extends BaseTestContainersIT {
    private static final Logger logger = LoggerFactory.getLogger(TexasHoldemGameIT.class);
    private static final int LATCH_TIMEOUT_IN_SECS = 100;
    private static final String LISTENER_USERNAME = "viewer";
    private static final String PLAYER_1_USERNAME = "thomas";
    private static final String PLAYER_2_USERNAME = "rory";
    private static final String PASSWORD = "password";
    private static final int PLAYER_WAIT = 1 * 1000;
    private static final int NUM_OF_ROUNDS = 3;

    @Test
    public void testTexasHoldemGame() throws Throwable {
        TableDTO table = getTexasHoldemTable();

        CountdownLatches latches = CountdownLatches.create();

        AbstractTestUser listener = new TestGameListenerUser(table.getId(), latches, LISTENER_USERNAME, PASSWORD, NUM_OF_ROUNDS);
        listener.connect();
        Thread.sleep(PLAYER_WAIT);

        List<AbstractTestUser> players = new ArrayList<>();
        players.add(new TestTexasHoldemPlayerUser(table.getId(), latches, PLAYER_1_USERNAME, PASSWORD));
        players.add(new TestTexasHoldemPlayerUser(table.getId(), latches, PLAYER_2_USERNAME, PASSWORD));

        for (AbstractTestUser player : players) {
            player.connect();
            Thread.sleep(PLAYER_WAIT);
        }

        latches.roundLatch().await(LATCH_TIMEOUT_IN_SECS, TimeUnit.SECONDS);

        for (AbstractTestUser player : players) {
            player.disconnect();
            Thread.sleep(PLAYER_WAIT);
        }

        latches.gameLatch().await(LATCH_TIMEOUT_IN_SECS, TimeUnit.SECONDS);

        listener.disconnect();

        for (AbstractTestUser player : players) {
            AtomicReference<Throwable> exceptionThrown = player.getExceptionThrown();
            if (exceptionThrown.get() != null) {
                throw new RuntimeException("Test Failure for player: " + player, exceptionThrown.get());
            }
        }

        List<ServerMessageDTO> listenerMessages = listener.getReceivedMessages();

        // Note: assertions aren't tight as we cut off a round when players disconnect, so treating as good enough

        assertThat(listenerMessages).filteredOn(message -> message.getType().equals(ServerMessageType.DEAL_INIT))
                .hasSizeBetween((players.size() * NUM_OF_ROUNDS) * 2, Integer.MAX_VALUE);

        assertThat(listenerMessages).filteredOn(message -> message.getType().equals(ServerMessageType.DEAL_COMMUNITY))
                .hasSizeBetween(NUM_OF_ROUNDS * 5, Integer.MAX_VALUE);

        assertThat(listenerMessages)
                .filteredOn(message -> message.getType()
                        .equals(ServerMessageType.ROUND_FINISHED))
                .hasSizeBetween(NUM_OF_ROUNDS, NUM_OF_ROUNDS + 1);

        assertThat(listenerMessages)
                .filteredOn(message -> message.getType()
                        .equals(ServerMessageType.GAME_FINISHED))
                .hasSize(1);
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private TableDTO getTexasHoldemTable() throws Exception {
        ApiHttpResponse<TableDTO[]> tablesResponse = get(TableDTO[].class, "/poker-table");
        assertEquals(HttpStatus.OK.value(), tablesResponse.httpResponse().statusCode());
        TableDTO[] tables = tablesResponse.resultBody();

        for (TableDTO tableDTO : tables) {
            if (tableDTO.getGameType() == GameType.TEXAS_HOLDEM) {
                return tableDTO;
            }
        }
        throw new NotFoundException("Failed to find a Texas Holdem Table");
    }
}