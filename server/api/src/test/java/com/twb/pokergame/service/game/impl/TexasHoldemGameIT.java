package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.domain.enumeration.GameType;
import com.twb.pokergame.dto.pokertable.TableDTO;
import com.twb.pokergame.exception.NotFoundException;
import com.twb.pokergame.testcontainers.BaseTestContainersIT;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TexasHoldemGameIT extends BaseTestContainersIT {
    private static final Logger logger = LoggerFactory.getLogger(TexasHoldemGameIT.class);
    private static final int GAME_TEST_TIMEOUT_IN_SECS = 100;

    @Test
    public void testTexasHoldemGame() throws Throwable {
        TableDTO table = getTexasHoldemTable();

        AtomicReference<Throwable> exceptionThrown = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketStompClient client = createClient();

        URI url = URI.create("ws://localhost:8081/looping");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken());

        client.connectAsync(url, headers, new StompHeaders(), new StompSessionHandler() {
            @Override
            public void afterConnected(@NotNull StompSession session, @NotNull StompHeaders connectedHeaders) {
                logger.info("TexasHoldemGameIT.afterConnected");
                logger.info("session = " + session + ", connectedHeaders = " + connectedHeaders);

                session.subscribe("/topic/loops." + table.getId(), new StompFrameHandler() {

                    @NotNull
                    @Override
                    public Type getPayloadType(@NotNull StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                        logger.info("TexasHoldemGameIT.handleFrame");
                        logger.info("headers = " + headers + ", payload = " + payload);
                    }
                });
            }

            @Override
            public void handleException(@NotNull StompSession session, StompCommand command,
                                        @NotNull StompHeaders headers, byte @NotNull [] payload,
                                        @NotNull Throwable exception) {
                logger.error("TexasHoldemGameIT.handleException");
                logger.error("session = " + session + ", command = " + command + ", headers = " + headers + ", payload = " + Arrays.toString(payload) + ", exception = " + exception);
                exceptionThrown.set(exception);
                latch.countDown();
            }

            @Override
            public void handleTransportError(@NotNull StompSession session, @NotNull Throwable exception) {
                logger.error("TexasHoldemGameIT.handleTransportError");
                logger.error("session = " + session + ", exception = " + exception);
                exceptionThrown.set(exception);
                latch.countDown();
            }

            @NotNull
            @Override
            public Type getPayloadType(@NotNull StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                logger.info("TexasHoldemGameIT.handleFrame");
                logger.info("headers = " + headers + ", payload = " + payload);
            }
        });

        latch.await(GAME_TEST_TIMEOUT_IN_SECS, TimeUnit.SECONDS);
        if (exceptionThrown.get() != null) {
            throw exceptionThrown.get();
        }
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private TableDTO getTexasHoldemTable() throws Exception {
        ApiHttpResponse<TableDTO[]> tablesResponse = get(TableDTO[].class, "/poker-table");
        assertEquals(HttpStatus.OK.value(), tablesResponse.getHttpResponse().statusCode());
        TableDTO[] tables = tablesResponse.getResultBody();

        for (TableDTO tableDTO : tables) {
            if (tableDTO.getGameType() == GameType.TEXAS_HOLDEM) {
                return tableDTO;
            }
        }
        throw new NotFoundException("Failed to find a Texas Holdem Table");
    }

    @NotNull
    private WebSocketStompClient createClient() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }
}