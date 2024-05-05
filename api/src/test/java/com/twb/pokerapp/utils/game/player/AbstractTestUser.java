package com.twb.pokerapp.utils.game.player;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.utils.http.message.ServerMessageConverter;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.ErrorMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.LogMessageDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractTestUser implements StompSessionHandler, StompFrameHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTestUser.class);
    private static final String CONNECTION_URL = "ws://localhost:8081/looping";
    private static final String SUBSCRIPTION_TOPIC_SUFFIX = "/topic/loops.%s";
    private static final String SEND_PLAYER_ACTION = "/app/pokerTable/%s/sendPlayerAction";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_CONNECTION_TYPE = "X-Connection-Type";
    private static final TaskScheduler TASK_SCHEDULER =
            new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());
    @Getter
    protected final TestUserParams params;
    private final Keycloak keycloak;
    private final WebSocketStompClient client;
    private final CountDownLatch connectLatch = new CountDownLatch(1);
    @Getter
    private final AtomicReference<Throwable> exceptionThrown = new AtomicReference<>();
    @Getter
    private final List<ServerMessageDTO> receivedMessages = Collections.synchronizedList(new ArrayList<>());
    private StompSession session;

    public AbstractTestUser(TestUserParams params) {
        this.keycloak = params.getKeycloak();
        this.params = params;
        this.client = createClient();
        this.session = null;
    }

    public void connect() throws InterruptedException {
        logger.info("Connecting {} to {}", params.getUsername(), params.getTable().getId());
        URI url = URI.create(CONNECTION_URL);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken());

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.put(HEADER_CONNECTION_TYPE, Collections.singletonList(getConnectionType().toString()));

        client.connectAsync(url, headers, stompHeaders, this);
        if (connectLatch.await(10, TimeUnit.SECONDS)) {
            logger.error("Timed out user {} from connecting to table {} via websocket", params.getUsername(), params.getTable().getId());
        }
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            logger.info("Disconnecting {} from {}", params.getUsername(), params.getTable().getId());
            session.disconnect();
        }
        session = null;
    }

    // ***************************************************************
    // Interface Methods
    // ***************************************************************

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.setAutoReceipt(true);
        session.subscribe(SUBSCRIPTION_TOPIC_SUFFIX.formatted(params.getTable().getId()), this);
        this.session = session;
    }

    @Override
    public void handleException(StompSession session, StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Exception thrown during stomp session", exception);
        exceptionThrown.set(exception);
        connectLatch.countDown();
        params.getLatches().roundLatch().countDown();
        params.getLatches().gameLatch().countDown();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        logger.error("Exception thrown after connect failure", exception);
        exceptionThrown.set(exception);
        connectLatch.countDown();
        params.getLatches().roundLatch().countDown();
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ServerMessageDTO.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload == null) {
            logger.warn("Frame received but payload is null with headers {}", headers);
            return;
        }
        connectLatch.countDown();
        ServerMessageDTO message = (ServerMessageDTO) payload;
        receivedMessages.add(message);

        if (message.getPayload() instanceof ErrorMessageDTO error) {
            logger.error("{} received error message: {}", params.getUsername(), error.getMessage());
            return;
        } else if (message.getPayload() instanceof LogMessageDTO log) {
            logger.info("{} received log message: {}", params.getUsername(), log.getMessage());
            return;
        }
        handleMessage(headers, message);
    }

    // ***************************************************************
    // Send Methods
    // ***************************************************************

    public void sendPlayerAction(CreatePlayerActionDTO createDto) {
        send(SEND_PLAYER_ACTION.formatted(params.getTable().getId()), createDto);
    }

    // ***************************************************************
    // Abstract Methods
    // ***************************************************************

    protected abstract void handleMessage(StompHeaders headers, ServerMessageDTO message);

    protected abstract ConnectionType getConnectionType();

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
        stompClient.setTaskScheduler(TASK_SCHEDULER);
        stompClient.setMessageConverter(new ServerMessageConverter());
        return stompClient;
    }

    protected void send(String destination, Object dto) {
        if (session == null || !session.isConnected()) {
            logger.warn("Cannot send to destination {} for user {} as not connected", destination, params.getUsername());
            return;
        }
        logger.info(">>>> [{}] sending {}", params.getUsername(), dto);
        StompSession.Receiptable receiptable = session.send(destination, dto);
        receiptable.addReceiptTask(() -> logger.info("Receipt received for user {} destination {} and payload {}", params.getUsername(), destination, dto));
        receiptable.addReceiptLostTask(() -> logger.info("Failed to receive receipt for user {} destination {} and payload {}", params.getUsername(), destination, dto));
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    private String getAccessToken() {
        TokenManager tokenManager = keycloak.tokenManager();
        AccessTokenResponse accessTokenResponse = tokenManager.getAccessToken();
        return accessTokenResponse.getToken();
    }
}
