package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.dto.pokertable.TableDTO;
import com.twb.pokerapp.exception.NotFoundException;
import com.twb.pokerapp.utils.game.GameRunnerParams;
import com.twb.pokerapp.utils.game.TexasHoldemGameRunner;
import com.twb.pokerapp.utils.game.player.AbstractTestUser.PlayerTurnHandler;
import com.twb.pokerapp.utils.testcontainers.BaseTestContainersIT;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TexasHoldemGameIT extends BaseTestContainersIT {
    private static final Logger logger = LoggerFactory.getLogger(TexasHoldemGameIT.class);
    private static final int COMMUNITY_CARD_COUNT = 5;
    private static final int PLAYER_CARD_COUNT = 2;

    @Test
    void testGameWithoutPlayerActions() throws Throwable {
        GameRunnerParams gameParams = GameRunnerParams.builder()
                .table(getTexasHoldemTable()).build();
        TexasHoldemGameRunner gameRunner = new TexasHoldemGameRunner(gameParams);

        Map<String, List<ServerMessageDTO>> receivedMessages = gameRunner.run();
    }

    @Test
    void testGameWithDefaultActions() throws Throwable {
        GameRunnerParams gameParams = GameRunnerParams.builder()
                .table(getTexasHoldemTable()).build();
        TexasHoldemGameRunner gameRunner = new TexasHoldemGameRunner(gameParams);

        PlayerTurnHandler handler1 = new PlayerTurnHandler() {
            @Override
            public void handle(StompHeaders headers, PlayerTurnDTO playerTurn) {
//                todo: do player turn handler
            }
        };

        PlayerTurnHandler handler2 = new PlayerTurnHandler() {
            @Override
            public void handle(StompHeaders headers, PlayerTurnDTO playerTurn) {
//                todo: do player turn handler
            }
        };

        Map<String, List<ServerMessageDTO>> receivedMessages = gameRunner.run(handler1, handler2);
    }

//    @Test
//    public void testTexasHoldemGame() throws Throwable {
//        TableDTO table = getTexasHoldemTable();
//
//        CountdownLatches latches = CountdownLatches.create();
//
//        AbstractTestUser listener = new TestGameListenerUser(table.getId(), latches, LISTENER_USERNAME, PASSWORD, NUM_OF_ROUNDS);
//        listener.connect();
//        Thread.sleep(PLAYER_WAIT_MS);
//
//        List<AbstractTestUser> players = new ArrayList<>();
//        players.add(new TestTexasHoldemPlayerUser(table.getId(), latches, PLAYER_1_USERNAME, PASSWORD));
//        players.add(new TestTexasHoldemPlayerUser(table.getId(), latches, PLAYER_2_USERNAME, PASSWORD));
//
//        for (AbstractTestUser player : players) {
//            player.connect();
//            Thread.sleep(PLAYER_WAIT_MS);
//        }
//
//        latches.roundLatch().await(LATCH_TIMEOUT_IN_SECS, TimeUnit.SECONDS);
//
//        for (AbstractTestUser player : players) {
//            player.disconnect();
//            Thread.sleep(PLAYER_WAIT_MS);
//        }
//
//        assertFalse(latches.gameLatch().await(LATCH_TIMEOUT_IN_SECS, TimeUnit.SECONDS));
//
//        listener.disconnect();
//
//        for (AbstractTestUser player : players) {
//            AtomicReference<Throwable> exceptionThrown = player.getExceptionThrown();
//            if (exceptionThrown.get() != null) {
//                throw new RuntimeException("Test Failure for player: " + player, exceptionThrown.get());
//            }
//        }
//
//        List<ServerMessageDTO> allMessages = listener.getReceivedMessages();
//
//        // Note: assertions aren't tight as we cut off a round when players disconnect, so treating as good enough
//        assertThat(allMessages).filteredOn(message -> message.getType().equals(ServerMessageType.DEAL_INIT))
//                .hasSizeBetween((players.size() * NUM_OF_ROUNDS) * PLAYER_CARD_COUNT, (players.size() * NUM_OF_ROUNDS + 1) * PLAYER_CARD_COUNT);
//
//        assertThat(allMessages).filteredOn(message -> message.getType().equals(ServerMessageType.DEAL_COMMUNITY))
//                .hasSizeBetween(NUM_OF_ROUNDS * COMMUNITY_CARD_COUNT, (NUM_OF_ROUNDS + 1) * COMMUNITY_CARD_COUNT);
//
//        assertThat(allMessages).filteredOn(message -> message.getType().equals(ServerMessageType.ROUND_FINISHED))
//                .hasSizeBetween(NUM_OF_ROUNDS, NUM_OF_ROUNDS + 1);
//
//        assertThat(allMessages).filteredOn(message -> message.getType().equals(ServerMessageType.GAME_FINISHED))
//                .hasSize(1);
//    }

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