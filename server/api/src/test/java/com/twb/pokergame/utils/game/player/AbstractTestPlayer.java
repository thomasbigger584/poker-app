package com.twb.pokergame.utils.game.player;

import com.twb.pokergame.utils.keycloak.KeycloakHelper;
import com.twb.pokergame.utils.message.ServerMessageConverter;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractTestPlayer implements StompSessionHandler, StompFrameHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTestPlayer.class);
    private static final String CONNECTION_URL = "ws://localhost:8081/looping";
    private static final String SUBSCRIPTION_TOPIC_SUFFIX = "/topic/loops.";
    private final UUID tableId;
    protected final CountDownLatch testLatch;
    @Getter
    private final String username;
    private final WebSocketStompClient client;
    private final Keycloak keycloak;
    private final CountDownLatch connectLatch = new CountDownLatch(1);
    @Getter
    private final AtomicReference<Throwable> exceptionThrown = new AtomicReference<>();

    public AbstractTestPlayer(UUID tableId, CountDownLatch testLatch, String username, String password) {
        this.tableId = tableId;
        this.testLatch = testLatch;
        this.username = username;
        this.client = createClient();
        this.keycloak = KeycloakHelper.getKeycloak(username, password);
    }

    public void connect() throws InterruptedException {
        logger.info("Connecting {} to {}", username, tableId);
        URI url = URI.create(CONNECTION_URL);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String accessToken = KeycloakHelper.getAccessToken(keycloak);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        client.connectAsync(url, headers, new StompHeaders(), this);
        connectLatch.await(10, TimeUnit.SECONDS);
    }

    // ***************************************************************
    // Interface Methods
    // ***************************************************************

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe(SUBSCRIPTION_TOPIC_SUFFIX + tableId, this);
    }

    @Override
    public void handleException(StompSession session, StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        exceptionThrown.set(exception);
        connectLatch.countDown();
        testLatch.countDown();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exceptionThrown.set(exception);
        connectLatch.countDown();
        testLatch.countDown();
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ServerMessageDTO.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        connectLatch.countDown();
        handleMessage(headers, (ServerMessageDTO) payload);
    }


    // ***************************************************************
    // Abstract Methods
    // ***************************************************************

    protected abstract void handleMessage(StompHeaders headers, ServerMessageDTO message);

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    @NotNull
    private WebSocketStompClient createClient() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new ServerMessageConverter());
        return stompClient;
    }
}
