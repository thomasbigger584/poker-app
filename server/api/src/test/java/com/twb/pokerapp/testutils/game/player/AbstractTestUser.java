package com.twb.pokerapp.testutils.game.player;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.testutils.http.message.ServerMessageConverter;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.ErrorMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
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

@Slf4j
public abstract class AbstractTestUser implements StompSessionHandler, StompFrameHandler {
    private static final String CONNECTION_URL = "ws://localhost:8081/looping";
    private static final String GAME_TOPIC_SUFFIX = "/topic/loops.%s";
    private static final String NOTIFICATION_TOPIC_SUFFIX = "/user/%s/notifications";
    private static final String SEND_PLAYER_ACTION = "/app/pokerTable/%s/sendPlayerAction";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_CONNECTION_TYPE = "X-Connection-Type";
    private static final String HEADER_BUYIN_AMOUNT = "X-BuyIn-Amount";
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
        connect(null);
    }

    public void connect(Double buyInAmount) throws InterruptedException {
        log.info("Connecting {} to {}", params.getUsername(), params.getTable().getId());
        var url = URI.create(CONNECTION_URL);
        var headers = new WebSocketHttpHeaders();
        var stompHeaders = new StompHeaders();
        stompHeaders.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken());
        stompHeaders.put(HEADER_CONNECTION_TYPE, Collections.singletonList(getConnectionType().toString()));
        if (getConnectionType() == ConnectionType.PLAYER) {
            stompHeaders.put(HEADER_BUYIN_AMOUNT, Collections.singletonList(Double.toString(buyInAmount)));
        }

        client.connectAsync(url, headers, stompHeaders, this);
        if (!connectLatch.await(10, TimeUnit.SECONDS)) {
            log.error("Timed out user {} from connecting to table {} via websocket", params.getUsername(), params.getTable().getId());
            throw new RuntimeException("Timed out user " + params.getUsername() + " from connecting to table " + params.getTable().getId());
        }
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            log.info("Disconnecting {} from {}", params.getUsername(), params.getTable().getId());
            session.disconnect();
        }
        session = null;
    }

    // ***************************************************************
    // Lifecycle Methods
    // ***************************************************************

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.setAutoReceipt(true);
        session.subscribe(GAME_TOPIC_SUFFIX.formatted(params.getTable().getId()), this);
        session.subscribe(NOTIFICATION_TOPIC_SUFFIX.formatted(params.getUsername()), this);
        this.session = session;
    }

    @Override
    public void handleException(StompSession session, StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        log.error("Exception thrown during stomp session", exception);
        exceptionThrown.set(exception);
        for (var index = 0; index < connectLatch.getCount(); index++) {
            connectLatch.countDown();
        }
        params.getLatches().roundLatch().countDown();
        params.getLatches().gameLatch().countDown();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error("Exception thrown after connect failure", exception);
        exceptionThrown.set(exception);
        for (var index = 0; index < connectLatch.getCount(); index++) {
            connectLatch.countDown();
        }
        params.getLatches().roundLatch().countDown();
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return ServerMessageDTO.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload == null) {
            log.error("Frame received but payload is null with headers {}", headers);
            return;
        }
        if (connectLatch.getCount() > 0) {
            connectLatch.countDown();
        }
        var message = (ServerMessageDTO) payload;
        receivedMessages.add(message);

        if (message.getPayload() instanceof ErrorMessageDTO errorDto) {
            log.error("{} received error message: {}", params.getUsername(), errorDto.getMessage());
            return;
        } else if (message.getPayload() instanceof LogMessageDTO logDto) {
            log.info("{} received log message: {}", params.getUsername(), logDto.getMessage());
            return;
        } else if (message.getPayload() instanceof ValidationDTO validationDto) {
            log.info("{} received validation message: {}", params.getUsername(), validationDto.getFields());
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
        var transports = new ArrayList<Transport>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        var sockJsClient = new SockJsClient(transports);
        var stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setTaskScheduler(TASK_SCHEDULER);
        stompClient.setMessageConverter(new ServerMessageConverter());
        return stompClient;
    }

    protected void send(String destination, Object dto) {
        if (session == null || !session.isConnected()) {
            log.warn("Cannot send to destination {} for user {} as not connected", destination, params.getUsername());
            return;
        }
        log.info(">>>> [{}] sending {}", params.getUsername(), dto);
        var receiptable = session.send(destination, dto);
        receiptable.addReceiptTask(() -> log.info("Receipt received for user {} destination {} and payload {}", params.getUsername(), destination, dto));
        receiptable.addReceiptLostTask(() -> log.info("Failed to receive receipt for user {} destination {} and payload {}", params.getUsername(), destination, dto));
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    private String getAccessToken() {
        var tokenManager = keycloak.tokenManager();
        var accessTokenResponse = tokenManager.getAccessToken();
        return accessTokenResponse.getToken();
    }
}
