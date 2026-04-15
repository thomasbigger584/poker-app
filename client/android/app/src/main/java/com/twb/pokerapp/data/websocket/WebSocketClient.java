package com.twb.pokerapp.data.websocket;

import android.util.Log;

import androidx.annotation.MainThread;

import com.google.gson.Gson;
import com.twb.pokerapp.BuildConfig;
import com.twb.pokerapp.data.auth.AuthConfiguration;
import com.twb.pokerapp.data.auth.AuthService;
import com.twb.pokerapp.data.exception.UnauthorizedException;
import com.twb.pokerapp.data.websocket.message.client.SendChatMessageDTO;
import com.twb.pokerapp.data.websocket.message.client.SendPlayerActionDTO;
import com.twb.pokerapp.data.websocket.message.server.ServerMessageDTO;
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

    private static final String WEBSOCKET_ENDPOINT = "/api/looping";
    private static final int HEARTBEAT_MS = 20000;

    private static final String TOPIC_PREFIX = "/topic/loops.";
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
                           OkHttpClient okHttpClient,
                           Gson gson) {
        this.authService = authService;
        this.authConfiguration = authConfiguration;
        this.okHttpClient = okHttpClient;
        this.gson = gson;
    }

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
                    Log.e(TAG, "Error refreshing token", throwable);
                    if (throwable instanceof UnauthorizedException) {
                        listener.onSubscribeError(throwable);
                    }
                }));
    }

    private void connectInternal(String accessToken, UUID tableId, WebSocketListener listener, String connectionType, Double buyInAmount) {
        var protocol = authConfiguration.isHttpsRequired() ? "wss://" : "ws://";
        var websocketUrl = protocol + BuildConfig.API_BASE_URL + WEBSOCKET_ENDPOINT + "/websocket";

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, websocketUrl, null, okHttpClient);

        List<StompHeader> connectHeaders = new ArrayList<>();
        connectHeaders.add(new StompHeader("Authorization", "Bearer " + accessToken));
        connectHeaders.add(new StompHeader("X-Connection-Type", connectionType));
        connectHeaders.add(new StompHeader("X-BuyIn-Amount", String.format(Locale.getDefault(), "%.2f", buyInAmount)));

        stompClient.withClientHeartbeat(HEARTBEAT_MS).withServerHeartbeat(HEARTBEAT_MS);

        // Handle Lifecycle
        compositeDisposable.add(stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    if (lifecycleEvent.getType() == LifecycleEvent.Type.OPENED) {
                        Log.i(TAG, "Stomp Connection OPENED - Initiating Subscriptions");
                        subscribeWithReceipts(tableId, listener);
                    } else if (lifecycleEvent.getType() == LifecycleEvent.Type.ERROR) {
                        listener.onConnectError(lifecycleEvent);
                    } else if (lifecycleEvent.getType() == LifecycleEvent.Type.CLOSED) {
                        listener.onClosed(lifecycleEvent);
                    }
                }));

        stompClient.connect(connectHeaders);
    }

    private void subscribeWithReceipts(UUID tableId, WebSocketListener listener) {
        // 1. Subscribe to Notifications FIRST with a Receipt (Just like AbstractTestUser)
        var notificationReceiptId = "receipt-notifications-" + UUID.randomUUID();
        List<StompHeader> notificationHeaders = new ArrayList<>();
        notificationHeaders.add(new StompHeader(StompHeader.DESTINATION, NOTIFICATIONS_TOPIC));
        notificationHeaders.add(new StompHeader(StompHeader.RECEIPT, notificationReceiptId));

        compositeDisposable.add(stompClient.topic(NOTIFICATIONS_TOPIC, notificationHeaders)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    listener.onMessage(gson.fromJson(topicMessage.getPayload(), ServerMessageDTO.class));
                }, throwable -> {
                    Log.e(TAG, "Notification Subscription Error", throwable);
                    listener.onSubscribeError(throwable);
                }));

        // 2. Subscribe to Table Topic with a Receipt
        var tableReceiptId = "receipt-table-" + UUID.randomUUID();
        List<StompHeader> tableHeaders = new ArrayList<>();
        tableHeaders.add(new StompHeader(StompHeader.DESTINATION, TOPIC_PREFIX + tableId));
        tableHeaders.add(new StompHeader(StompHeader.RECEIPT, tableReceiptId));

        compositeDisposable.add(stompClient.topic(TOPIC_PREFIX + tableId, tableHeaders)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    listener.onMessage(gson.fromJson(topicMessage.getPayload(), ServerMessageDTO.class));
                }, throwable -> {
                    Log.e(TAG, "Table Subscription Error", throwable);
                    listener.onSubscribeError(throwable);
                }));

        // 3. Listen for Receipts to confirm "Subscription Complete"
        compositeDisposable.add(stompClient.receipts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(receiptId -> {
                    Log.d(TAG, "Receipt received from server: " + receiptId);
                    if (receiptId.equals(tableReceiptId)) {
                        Log.i(TAG, "All subscriptions confirmed. Ready to play.");
                        listener.onOpened(new LifecycleEvent(LifecycleEvent.Type.OPENED));
                    }
                }, throwable -> Log.e(TAG, "Receipt Error", throwable)));
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

    public void sendChatMessage(UUID tableId, SendChatMessageDTO dto, SendListener listener) {
        var destination = String.format(Locale.getDefault(), SEND_ENDPOINT_PREFIX + SEND_CHAT_MESSAGE, tableId);
        sendMessage(destination, gson.toJson(dto), listener);
    }

    public void sendPlayerAction(UUID tableId, SendPlayerActionDTO dto, SendListener listener) {
        var destination = String.format(Locale.getDefault(), SEND_ENDPOINT_PREFIX + SEND_PLAYER_ACTION, tableId);
        sendMessage(destination, gson.toJson(dto), listener);
    }

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
