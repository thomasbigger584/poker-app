package com.twb.pokerapp.testutils.game.player;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.http.message.ServerMessageConverter;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.ErrorMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class AbstractTestUser implements StompSessionHandler, StompFrameHandler {
    private static final String CONNECTION_URL = "ws://localhost:8081/looping";
    private static final String GAME_TOPIC_SUFFIX = "/topic/loops.%s";
    private static final String NOTIFICATION_TOPIC = "/user/notifications";
    private static final String SEND_PLAYER_ACTION = "/app/pokerTable/%s/sendPlayerAction";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_CONNECTION_TYPE = "X-Connection-Type";
    private static final String HEADER_BUYIN_AMOUNT = "X-BuyIn-Amount";
    private static final int HEARTBEAT_IN_MS = 5 * 1000;
    private static final int CONNECT_LATCH_TIMEOUT_SECS = 30;
    @Getter
    protected final TestUserParams params;
    private final WebSocketStompClient client;
    private final CountDownLatch connectLatch = new CountDownLatch(1);
    @Getter
    private final AtomicReference<Throwable> exceptionThrown = new AtomicReference<>();
    @Getter
    private final List<ServerMessageDTO> receivedMessages = Collections.synchronizedList(new ArrayList<>());
    protected StompSession session;

    public AbstractTestUser(TestUserParams params) {
        this.params = params;
        this.client = createClient();
        this.session = null;
    }

    public String getUsername() {
        return params.getUsername();
    }

    public void connect() throws InterruptedException {
        connect(null);
    }

    public void connect(BigDecimal buyInAmount) throws InterruptedException {
        log.debug("Connecting {} to {}", params.getUsername(), params.getTable().getId());
        var url = URI.create(CONNECTION_URL);
        var headers = new WebSocketHttpHeaders();
        var stompHeaders = new StompHeaders();
        stompHeaders.setHost("/");
        
        stompHeaders.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken());
        stompHeaders.put(HEADER_CONNECTION_TYPE, Collections.singletonList(getConnectionType().toString()));
        if (getConnectionType() == ConnectionType.PLAYER && buyInAmount != null) {
            stompHeaders.put(HEADER_BUYIN_AMOUNT, Collections.singletonList(buyInAmount.toString()));
        }

        client.connectAsync(url, headers, stompHeaders, this);
        if (!connectLatch.await(CONNECT_LATCH_TIMEOUT_SECS, TimeUnit.SECONDS)) {
            log.error("Timed out user {} from connecting to table {} via websocket", params.getUsername(), params.getTable().getId());
            throw new RuntimeException("Timed out user " + params.getUsername() + " from connecting to table " + params.getTable().getId());
        }
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            log.debug("Disconnecting {} from {}", params.getUsername(), params.getTable().getId());
            session.disconnect();
        }
        session = null;
    }

    public void stop() {
        if (client != null) {
            log.debug("Stopping client for user {}", params.getUsername());
            client.stop();
        }
    }

    // ***************************************************************
    // Lifecycle Methods
    // ***************************************************************

    @Override
    public void afterConnected(StompSession session, @NonNull StompHeaders connectedHeaders) {
        this.session = session;

        var gameTopic = GAME_TOPIC_SUFFIX.formatted(params.getTable().getId());

        var gameHeaders = new StompHeaders();
        gameHeaders.setDestination(gameTopic);
        gameHeaders.setReceipt("receipt-" + params.getUsername() + "-game-" + UUID.randomUUID());

        var gameReceipt = session.subscribe(gameHeaders, this);
        gameReceipt.addReceiptTask(() -> {
            log.debug("Receipt received for subscription on user {} destination {}", params.getUsername(), gameTopic);

            var notificationHeaders = new StompHeaders();
            notificationHeaders.setDestination(NOTIFICATION_TOPIC);
            notificationHeaders.setReceipt("receipt-" + params.getUsername() + "-notifications-" + UUID.randomUUID());

            var notificationReceipt = session.subscribe(notificationHeaders, this);
            notificationReceipt.addReceiptTask(() -> {
                log.debug("Receipt received for subscription on user {} destination {}", params.getUsername(), NOTIFICATION_TOPIC);
                countdownLatch(connectLatch);
            });
            notificationReceipt.addReceiptLostTask(() -> {
                throw new RuntimeException("Failed to receive notification receipt for subscription on user " + params.getUsername() + " destination " + NOTIFICATION_TOPIC);
            });
        });
        gameReceipt.addReceiptLostTask(() -> {
            throw new RuntimeException("Failed to receive game receipt for subscription on user " + params.getUsername() + " destination " + gameTopic);
        });
    }

    @Override
    public void handleException(@NonNull StompSession session, StompCommand command,
                                @NonNull StompHeaders headers, byte @NonNull [] payload, @NonNull Throwable exception) {
        log.error("Exception thrown during stomp session", exception);
        if (this.session != null) {
            exceptionThrown.compareAndSet(null, exception);
        }
        countdownLatches();
    }

    @Override
    public void handleTransportError(@NonNull StompSession session, @NonNull Throwable exception) {
        log.error("Exception thrown after connect failure", exception);
        if (this.session != null) {
            exceptionThrown.compareAndSet(null, exception);
        }
        countdownLatches();
    }

    @Override
    public @NonNull Type getPayloadType(@NonNull StompHeaders headers) {
        return ServerMessageDTO.class;
    }

    @Override
    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
        if (payload == null) {
            log.error("Frame received but payload is null with headers {}", headers);
            return;
        }
        try {
            var message = (ServerMessageDTO) payload;
            receivedMessages.add(message);

            var gameLatch = params.getLatches().gameLatch();
            if (message.getPayload() instanceof ErrorMessageDTO errorDto) {
                GameLatches.countdown(gameLatch);
                return;
            } else if (message.getPayload() instanceof LogMessageDTO logDto) {
                log.debug("{} received log message: {}", params.getUsername(), logDto.getMessage());
                return;
            } else if (message.getPayload() instanceof ValidationDTO validationDto) {
                log.debug("{} received validation message: {}", params.getUsername(), validationDto.getFields());
                GameLatches.countdown(gameLatch);
                return;
            }
            handleMessage(headers, message);
        } catch (Exception e) {
            log.error("Failed to handle frame", e);
            throw e;
        }
    }

    // ***************************************************************
    // Send Methods
    // ***************************************************************

    public void sendPlayerAction(CreatePlayerActionDTO createDto) {
        send(SEND_PLAYER_ACTION.formatted(params.getTable().getId()), createDto);
    }

    protected void send(String destination, Object dto) {
        if (session == null || !session.isConnected()) {
            log.warn("Cannot send to destination {} for user {} as not connected", destination, params.getUsername());
            return;
        }
        log.debug(">>>> [{}] sending {}", params.getUsername(), dto);

        var headers = new StompHeaders();
        headers.setDestination(destination);
        headers.setReceipt("receipt-" + params.getUsername() + "-send-" + UUID.randomUUID());

        var receiptable = session.send(headers, dto);
        receiptable.addReceiptTask(() -> log.debug("Receipt received for user {} destination {} and payload {}", params.getUsername(), destination, dto));
        receiptable.addReceiptLostTask(() -> {
            throw new RuntimeException(String.format("Failed to receive receipt for user %s destination %s and payload %s", params.getUsername(), destination, dto));
        });
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
        var transports = new ArrayList<Transport>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        var sockJsClient = new SockJsClient(transports);

        var taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.setThreadNamePrefix("stomp-test-heartbeat-");
        taskScheduler.initialize();

        var stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setTaskScheduler(taskScheduler);

        stompClient.setDefaultHeartbeat(new long[]{HEARTBEAT_IN_MS, (long) (HEARTBEAT_IN_MS * 3)});
        stompClient.setMessageConverter(new ServerMessageConverter());
        stompClient.setInboundMessageSizeLimit(1024 * 1024);
        return stompClient;
    }

    private void countdownLatches() {
        countdownLatch(connectLatch);
        var latches = params.getLatches();
        countdownLatch(latches.gameLatch());
    }

    protected void countdownLatch(CountDownLatch latch) {
        for (var index = 0; index < latch.getCount(); index++) {
            latch.countDown();
        }
    }

    private String getAccessToken() {
        var tokenManager = params.getKeycloak().tokenManager();
        var accessTokenResponse = tokenManager.getAccessToken();
        return accessTokenResponse.getToken();
    }
}
