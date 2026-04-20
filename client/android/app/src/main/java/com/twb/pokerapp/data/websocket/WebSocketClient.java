package com.twb.pokerapp.data.websocket;

import android.util.Log;

import androidx.annotation.MainThread;

import com.google.gson.Gson;
import com.twb.pokerapp.BuildConfig;
import com.twb.pokerapp.data.auth.AuthConfiguration;
import com.twb.pokerapp.data.auth.AuthEventBus;
import com.twb.pokerapp.data.auth.AuthService;
import com.twb.pokerapp.data.websocket.message.client.SendChatMessageDTO;
import com.twb.pokerapp.data.websocket.message.client.SendPlayerActionDTO;
import com.twb.pokerapp.data.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.di.network.qualifiers.Authenticated;
import com.twb.stomplib.dto.LifecycleEvent;
import com.twb.stomplib.dto.StompHeader;
import com.twb.stomplib.stomp.Stomp;
import com.twb.stomplib.stomp.StompClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.CompletableTransformer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

public class WebSocketClient {
    private static final String TAG = WebSocketClient.class.getSimpleName();
    private static final String UNAUTHORIZED_ERROR_CODE = "401";

    private static final String WEBSOCKET_ENDPOINT = "/api/looping";
    private static final int HEARTBEAT_MS = 20000;
    private static final String GAME_APP_SUBSCRIBE = "/app/loops.%s";
    private static final String GAME_TOPIC_SUBSCRIBE = "/topic/loops.%s";
    private static final String NOTIFICATIONS_TOPIC = "/user/queue/notifications";

    private static final String SEND_ENDPOINT_PREFIX = "/app/pokerTable.%s";
    private static final String SEND_CHAT_MESSAGE = ".sendChatMessage";
    private static final String SEND_PLAYER_ACTION = ".sendPlayerAction";

    private final AuthService authService;
    private final AuthConfiguration authConfiguration;
    private final OkHttpClient okHttpClient;
    private final Gson gson;

    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;

    @Inject
    public WebSocketClient(AuthService authService,
                           AuthConfiguration authConfiguration,
                           @Authenticated OkHttpClient okHttpClient,
                           Gson gson) {
        this.authService = authService;
        this.authConfiguration = authConfiguration;
        this.okHttpClient = okHttpClient;
        this.gson = gson;
    }


    // ***************************************************************
    // WebSocket Lifecycle
    // ***************************************************************

    public void connect(UUID tableId, WebSocketListener listener, String connectionType, Double buyInAmount) {
        if (stompClient != null && stompClient.isConnected()) {
            return;
        }

        resetSubscriptions();

        compositeDisposable.add(Single.fromCallable(authService::getAccessTokenWithRefresh)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accessToken -> {
                    if (accessToken == null) {
                        listener.onConnectError(new LifecycleEvent(LifecycleEvent.Type.ERROR));
                        return;
                    }
                    connectInternal(accessToken, tableId, listener, connectionType, buyInAmount);
                }, throwable -> {
                    listener.onSubscribeError(throwable);
                }));
    }

    private void connectInternal(String accessToken, UUID tableId, WebSocketListener listener, String connectionType, Double buyInAmount) {
        var protocol = authConfiguration.isHttpsRequired() ? "wss://" : "ws://";
        var websocketUrl = protocol + BuildConfig.API_BASE_URL + WEBSOCKET_ENDPOINT + "/websocket";

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, websocketUrl, null, okHttpClient);

        var connectHeaders = new ArrayList<StompHeader>();
        connectHeaders.add(new StompHeader(StompHeader.ID, "host"));
        connectHeaders.add(new StompHeader("host", "/"));
        connectHeaders.add(new StompHeader("Authorization", "Bearer " + accessToken));
        connectHeaders.add(new StompHeader("X-Connection-Type", connectionType));
        connectHeaders.add(new StompHeader("X-BuyIn-Amount", String.format(Locale.getDefault(), "%.2f", buyInAmount)));

        stompClient.withClientHeartbeat(HEARTBEAT_MS)
                .withServerHeartbeat(HEARTBEAT_MS);

        compositeDisposable.add(stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    if (lifecycleEvent.getType() == LifecycleEvent.Type.OPENED) {
                        Log.i(TAG, "Stomp Connection OPENED - Initiating Subscriptions");
                        subscribeWithReceipts(tableId, listener);
                    } else if (lifecycleEvent.getType() == LifecycleEvent.Type.ERROR) {
                        var exception = lifecycleEvent.getException();
                        Log.e(TAG, "Stomp Connection Error: " + exception);
                        if (exception != null) {
                            var message = exception.getMessage();
                            if (message != null && message.contains(UNAUTHORIZED_ERROR_CODE)) {
                                AuthEventBus.triggerLogout();
                            }
                        }
                        listener.onConnectError(lifecycleEvent);
                    } else if (lifecycleEvent.getType() == LifecycleEvent.Type.CLOSED) {
                        listener.onClosed(lifecycleEvent);
                        resetSubscriptions();
                    } else if (lifecycleEvent.getType() == LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT) {
                        listener.onFailedServerHeartbeat(lifecycleEvent);
                    }
                }));

        stompClient.connect(connectHeaders);
    }

    private void subscribeWithReceipts(UUID tableId, WebSocketListener listener) {
        var tableIdStr = tableId.toString();

        // 1. App Subscription (Handshake / Initial State)
        var appTopic = String.format(GAME_APP_SUBSCRIBE, tableIdStr);
        var appReceiptId = "receipt-app-" + UUID.randomUUID();
        var appHeaders = new ArrayList<StompHeader>();
        appHeaders.add(new StompHeader(StompHeader.DESTINATION, appTopic));
        appHeaders.add(new StompHeader(StompHeader.RECEIPT, appReceiptId));

        // 2. Live Topic Subscription (Broadcasting Game Events)
        var liveTopic = String.format(GAME_TOPIC_SUBSCRIBE, tableIdStr);
        var liveReceiptId = "receipt-live-" + UUID.randomUUID();
        var liveHeaders = new ArrayList<StompHeader>();
        liveHeaders.add(new StompHeader(StompHeader.DESTINATION, liveTopic));
        liveHeaders.add(new StompHeader(StompHeader.RECEIPT, liveReceiptId));

        // 3. User Notifications
        var notifReceiptId = "receipt-notif-" + UUID.randomUUID();
        var notifHeaders = new ArrayList<StompHeader>();
        notifHeaders.add(new StompHeader(StompHeader.DESTINATION, NOTIFICATIONS_TOPIC));
        notifHeaders.add(new StompHeader(StompHeader.RECEIPT, notifReceiptId));

        // Start Subscriptions
        subscribeToTopic(appTopic, appHeaders, listener);
        subscribeToTopic(liveTopic, liveHeaders, listener);
        subscribeToTopic(NOTIFICATIONS_TOPIC, notifHeaders, listener);

        // Chain logic for receipts
        compositeDisposable.add(stompClient.receipts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(receiptId -> {
                    Log.d(TAG, "Receipt received: " + receiptId);
                    // Once the Live Topic is confirmed, we consider the connection "Fully Open"
                    if (receiptId.equals(liveReceiptId)) {
                        Log.i(TAG, "Game connection established and confirmed.");
                        listener.onOpened(new LifecycleEvent(LifecycleEvent.Type.OPENED));
                    }
                }, throwable -> Log.e(TAG, "Receipt Error", throwable)));
    }

    private void subscribeToTopic(String topic, List<StompHeader> headers, WebSocketListener listener) {
        compositeDisposable.add(stompClient.topic(topic, headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stompMessage -> {
                    var message = gson.fromJson(stompMessage.getPayload(), ServerMessageDTO.class);
                    listener.onMessage(message);
                }, throwable -> {
                    Log.e(TAG, "Subscription error on topic: " + topic, throwable);
                    listener.onSubscribeError(throwable);
                }));
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

    // WebSocket Send Methods
    // ----------------------------------------------------------------

    public void sendChatMessage(UUID tableId, SendChatMessageDTO dto, SendListener listener) {
        var destination = String.format(Locale.getDefault(), SEND_ENDPOINT_PREFIX + SEND_CHAT_MESSAGE, tableId);
        sendMessage(destination, gson.toJson(dto), listener);
    }

    public void sendPlayerAction(UUID tableId, SendPlayerActionDTO dto, SendListener listener) {
        var destination = String.format(Locale.getDefault(), SEND_ENDPOINT_PREFIX + SEND_PLAYER_ACTION, tableId);
        sendMessage(destination, gson.toJson(dto), listener);
    }

    // WebSocket Send Helper Methods
    // ----------------------------------------------------------------

    private void sendMessage(String destination, String message, SendListener listener) {
        compositeDisposable.add(stompClient.send(destination, message)
                .compose(applySchedulers())
                .subscribe(listener::onSendSuccess, listener::onSendFailure));
    }

    private CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // ***************************************************************
    // Listeners
    // ***************************************************************

    @MainThread
    public interface SendListener {
        void onSendSuccess();

        void onSendFailure(Throwable throwable);
    }

    @MainThread
    public interface WebSocketListener {
        void onOpened(LifecycleEvent event);

        void onConnectError(LifecycleEvent event);

        void onClosed(LifecycleEvent event);

        void onFailedServerHeartbeat(LifecycleEvent event);

        void onMessage(ServerMessageDTO<?> message);

        void onSubscribeError(Throwable throwable);
    }
}
