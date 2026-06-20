package com.twb.pokerapp.testutils.game.player;

import com.twb.pokerapp.mapper.enumeration.ConnectionTypes;
import com.twb.pokerapp.proto.ConnectionType;
import com.twb.pokerapp.proto.CreateBotConnectionDTO;
import com.twb.pokerapp.proto.CreatePlayerActionDTO;
import com.twb.pokerapp.proto.ServerMessageDTO;
import com.twb.pokerapp.testutils.game.GameLatches;
import com.twb.pokerapp.testutils.http.message.ServerMessageConverter;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

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

    // Destination Constants (Updated for RabbitMQ Dot-Notation)
    private static final String GAME_APP_SUBSCRIBE = "/app/loops.%s";
    private static final String GAME_TOPIC_SUFFIX = "/topic/loops.%s";
    private static final String NOTIFICATION_TOPIC = "/user/queue/notifications";
    private static final String SEND_PLAYER_ACTION = "/app/pokerTable.%s.sendPlayerAction";
    private static final String SEND_BOT_CONNECTED = "/app/pokerTable.%s.sendBotConnected";
    private static final String SEND_DISCONNECT_PLAYER = "/app/pokerTable.%s.sendDisconnectPlayer";
    private static final int DISCONNECT_RECEIPT_TIMEOUT_SECS = 10;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HEADER_CONNECTION_TYPE = "X-Connection-Type";
    private static final String HEADER_BUYIN_AMOUNT = "X-BuyIn-Amount";

    // Using 20s to prevent RabbitMQ from closing connections during Docker lag
    private static final int HEARTBEAT_IN_MS = 20 * 1000;
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
        var bearerToken = BEARER_PREFIX + getAccessToken();

        // Authenticate the native WebSocket handshake itself (Spring Security gates /looping on the
        // USER role), then repeat the token on the STOMP CONNECT frame for WebSocketAuthChannelInterceptor.
        var headers = new WebSocketHttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, bearerToken);

        var stompHeaders = new StompHeaders();
        stompHeaders.setHost("/");

        stompHeaders.add(HttpHeaders.AUTHORIZATION, bearerToken);
        stompHeaders.put(HEADER_CONNECTION_TYPE, Collections.singletonList(ConnectionTypes.toWire(getConnectionType())));
        if (getConnectionType() == ConnectionType.CONNECTION_TYPE_PLAYER && buyInAmount != null) {
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
            sendDisconnectPlayer();
            session.disconnect();
        }
        if (client != null && client.isRunning()) {
            client.stop();
        }
        session = null;
    }

    /**
     * Send the explicit "leave table" message and block until the server confirms it (via the
     * STOMP receipt sent once {@code onUserDisconnected} has committed), so the seat is given up
     * before the socket is torn down. Best-effort: logs and proceeds if no receipt arrives.
     */
    private void sendDisconnectPlayer() {
        var destination = SEND_DISCONNECT_PLAYER.formatted(params.getTable().getId());
        var headers = new StompHeaders();
        headers.setDestination(destination);
        headers.setReceipt("receipt-" + params.getUsername() + "-disconnect-" + UUID.randomUUID());

        var receiptLatch = new CountDownLatch(1);
        var receiptable = session.send(headers, Collections.emptyMap());
        receiptable.addReceiptTask(receiptLatch::countDown);
        receiptable.addReceiptLostTask(receiptLatch::countDown);
        try {
            if (!receiptLatch.await(DISCONNECT_RECEIPT_TIMEOUT_SECS, TimeUnit.SECONDS)) {
                log.warn("Timed out awaiting explicit disconnect receipt for user {}", params.getUsername());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        if (client != null) {
            log.debug("Stopping client for user {}", params.getUsername());
            client.stop();
        }
    }

    @Override
    public void afterConnected(StompSession session, @NonNull StompHeaders connectedHeaders) {
        this.session = session;
        var tableId = params.getTable().getId();

        // 1. Initial State Sync (Triggers @SubscribeMapping in TableWebSocketController)
        var appTopic = GAME_APP_SUBSCRIBE.formatted(tableId);
        var appHeaders = new StompHeaders();
        appHeaders.setDestination(appTopic);
        appHeaders.setReceipt("receipt-" + params.getUsername() + "-app-" + UUID.randomUUID());

        var appReceipt = session.subscribe(appHeaders, this);
        appReceipt.addReceiptTask(() -> {
            log.debug("Initial sync receipt received for user {} on {}", params.getUsername(), appTopic);

            // 2. Live Broadcast Subscription (Standard RabbitMQ Topic)
            var liveTopic = GAME_TOPIC_SUFFIX.formatted(tableId);
            var liveHeaders = new StompHeaders();
            liveHeaders.setDestination(liveTopic);
            liveHeaders.setReceipt("receipt-" + params.getUsername() + "-live-" + UUID.randomUUID());

            var liveReceipt = session.subscribe(liveHeaders, this);
            liveReceipt.addReceiptTask(() -> {
                log.debug("Live stream receipt received for user {} on {}", params.getUsername(), liveTopic);

                // 3. User Notification Subscription (Private Notifications)
                var notificationHeaders = new StompHeaders();
                notificationHeaders.setDestination(NOTIFICATION_TOPIC);
                notificationHeaders.setReceipt("receipt-" + params.getUsername() + "-notifications-" + UUID.randomUUID());

                var notificationReceipt = session.subscribe(notificationHeaders, this);
                notificationReceipt.addReceiptTask(() -> {
                    log.debug("Notification receipt received for user {} on {}", params.getUsername(), NOTIFICATION_TOPIC);
                    countdownLatch(connectLatch);
                });

                notificationReceipt.addReceiptLostTask(() -> {
                    throw new RuntimeException("Failed to receive notification receipt for user " + params.getUsername());
                });
            });

            liveReceipt.addReceiptLostTask(() -> {
                throw new RuntimeException("Failed to receive live stream receipt for user " + params.getUsername());
            });
        });

        appReceipt.addReceiptLostTask(() -> {
            throw new RuntimeException("Failed to receive initial app receipt for user " + params.getUsername());
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
            switch (message.getPayloadCase()) {
                case ERROR -> {
                    GameLatches.countdown(gameLatch);
                    return;
                }
                case LOG -> {
                    log.debug("{} received log message: {}", params.getUsername(), message.getLog().getMessage());
                    return;
                }
                case VALIDATION -> {
                    log.debug("{} received validation message: {}", params.getUsername(), message.getValidation().getFieldsList());
                    GameLatches.countdown(gameLatch);
                    return;
                }
                default -> {
                    // fall through to game-specific handling
                }
            }
            handleMessage(headers, message);
        } catch (Exception e) {
            log.error("Failed to handle frame", e);
            throw e;
        }
    }

    public void sendPlayerAction(CreatePlayerActionDTO createDto) {
        send(SEND_PLAYER_ACTION.formatted(params.getTable().getId()), createDto);
    }

    public void sendBotConnected(UUID botUserId, BigDecimal buyInAmount) {
        var createDto = CreateBotConnectionDTO.newBuilder()
                .setBotUserId(botUserId.toString())
                .setBuyInAmount(buyInAmount.toPlainString())
                .build();
        send(SEND_BOT_CONNECTED.formatted(params.getTable().getId()), createDto);
    }

    protected void send(String destination, Object dto) {
        if (session == null || !session.isConnected()) {
            log.warn("Cannot send to destination {} for user {} as not connected", destination, params.getUsername());
            return;
        }
        log.debug(">>>> [{}] sending {} to {}", params.getUsername(), dto, destination);

        var headers = new StompHeaders();
        headers.setDestination(destination);
        headers.setReceipt("receipt-" + params.getUsername() + "-send-" + UUID.randomUUID());
        // octet-stream forces a binary WebSocket frame (see ProtobufMessageConverter) so the binary
        // protobuf body is not UTF-8-mangled by text framing.
        headers.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM);

        var receiptable = session.send(headers, dto);
        receiptable.addReceiptTask(() -> log.debug("Receipt received for user {} destination {}", params.getUsername(), destination));
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
        // Native WebSocket (no SockJS): the server endpoint carries raw binary protobuf STOMP
        // frames, which SockJS's text-only transport would force into base64.
        var taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.setThreadNamePrefix("stomp-test-heartbeat-");
        taskScheduler.initialize();

        var stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setTaskScheduler(taskScheduler);

        // 0 means client will NOT expect heartbeats FROM the server (more stable for testing).
        // TODO: revise the 0 for running apps
        stompClient.setDefaultHeartbeat(new long[]{HEARTBEAT_IN_MS, 0});

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
        while (latch.getCount() > 0) {
            latch.countDown();
        }
    }

    private String getAccessToken() {
        var tokenManager = params.getKeycloak().tokenManager();
        return tokenManager.getAccessToken().getToken();
    }
}
