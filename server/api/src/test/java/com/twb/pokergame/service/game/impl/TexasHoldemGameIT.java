package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.dto.pokertable.TableDTO;
import com.twb.pokergame.exception.NotFoundException;
import com.twb.pokergame.utils.game.player.AbstractTestPlayer;
import com.twb.pokergame.utils.game.player.impl.TestTexasHoldemPlayer;
import com.twb.pokergame.utils.testcontainers.BaseTestContainersIT;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TexasHoldemGameIT extends BaseTestContainersIT {
    private static final Logger logger = LoggerFactory.getLogger(TexasHoldemGameIT.class);
    private static final int GAME_TEST_TIMEOUT_IN_SECS = 100;
    private static final String PLAYER_1_USERNAME = "thomas";
    private static final String PLAYER_2_USERNAME = "rory";
    private static final String PASSWORD = "password";
    private static final int PLAYER_CONNECT_WAIT = 3 * 1000;
    private static final int NUM_OF_ROUNDS = 3;

    @Test
    public void testTexasHoldemGame() throws Throwable {
        TableDTO table = getTexasHoldemTable();

        CountDownLatch testLatch = new CountDownLatch(1);

        List<AbstractTestPlayer> players = new ArrayList<>();
        players.add(new TestTexasHoldemPlayer(table.getId(), testLatch, PLAYER_1_USERNAME, PASSWORD, NUM_OF_ROUNDS));
        players.add(new TestTexasHoldemPlayer(table.getId(), testLatch, PLAYER_2_USERNAME, PASSWORD, NUM_OF_ROUNDS));

        for (AbstractTestPlayer player : players) {
            player.connect();
            Thread.sleep(PLAYER_CONNECT_WAIT);
        }

        testLatch.await(GAME_TEST_TIMEOUT_IN_SECS, TimeUnit.SECONDS);

        for (AbstractTestPlayer player : players) {
            AtomicReference<Throwable> exceptionThrown = player.getExceptionThrown();
            if (exceptionThrown.get() != null) {
                throw new RuntimeException("Test Failure for player: " + player, exceptionThrown.get());
            }
        }

        assertTrue(true);
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