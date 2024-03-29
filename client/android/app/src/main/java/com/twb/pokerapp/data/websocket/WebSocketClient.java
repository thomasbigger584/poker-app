package com.twb.pokerapp.data.websocket;

import android.util.Log;

import androidx.annotation.MainThread;

import com.google.gson.Gson;
import com.twb.pokerapp.BuildConfig;
import com.twb.pokerapp.data.auth.AuthService;
import com.twb.pokerapp.data.websocket.message.client.SendChatMessageDTO;
import com.twb.pokerapp.data.websocket.message.client.SendPlayerActionDTO;
import com.twb.pokerapp.data.websocket.message.server.ServerMessageDTO;
import com.twb.stomplib.dto.LifecycleEvent;
import com.twb.stomplib.dto.StompHeader;
import com.twb.stomplib.stomp.Stomp;
import com.twb.stomplib.stomp.StompClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WebSocketClient {
    private static final String TAG = WebSocketClient.class.getSimpleName();

    private static final String PROTOCOL = "ws://";
    private static final String WEBSOCKET_ENDPOINT = "/looping/websocket";
    private static final int CLIENT_HEARTBEAT_MS = 1000;
    private static final int SERVER_HEARTBEAT_MS = 1000;
    private static final String TOPIC_PREFIX = "/topic/loops.";
    private static final String SEND_ENDPOINT_PREFIX = "/app/pokerTable/%s";
    private static final String SEND_CHAT_MESSAGE = "/sendChatMessage";
    private static final String SEND_PLAYER_ACTION = "/sendPlayerAction";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CONNECTION_TYPE_HEADER = "X-Connection-Type";
    private static final String PLAYER_CONNECTION_TYPE = "PLAYER";
    private final AuthService authService;
    private final Gson gson;

    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;

    @Inject
    public WebSocketClient(AuthService authService, Gson gson) {
        this.authService = authService;
        this.gson = gson;
    }

    // ***************************************************************
    // WebSocket Lifecycle
    // ***************************************************************

    public void connect(UUID pokerTableId, WebSocketListener listener) {
        if (stompClient != null && stompClient.isConnected()) {
            return;
        }
        String accessToken = authService.getAccessToken();
        if (accessToken == null) {
            throw new RuntimeException("Cannot connect to websocket as access token is null");
        }
        String websocketUrl = PROTOCOL + BuildConfig.API_BASE_URL + WEBSOCKET_ENDPOINT;
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, websocketUrl);

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken));
        headers.add(new StompHeader(CONNECTION_TYPE_HEADER, PLAYER_CONNECTION_TYPE));

        stompClient.withClientHeartbeat(CLIENT_HEARTBEAT_MS)
                .withServerHeartbeat(SERVER_HEARTBEAT_MS);

        resetSubscriptions();

        compositeDisposable.add(stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "CONNECT: Stomp Connection OPENED");
                            listener.onOpened(lifecycleEvent);
                            break;
                        case ERROR:
                            Log.e(TAG, "CONNECT: Stomp connection error", lifecycleEvent.getException());
                            listener.onConnectError(lifecycleEvent);
                            break;
                        case CLOSED:
                            Log.i(TAG, "CONNECT: Stomp Connection CLOSED");
                            listener.onClosed(lifecycleEvent);
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.e(TAG, "CONNECT: Stomp Failed server heartbeat");
                            listener.onFailedServerHeartbeat(lifecycleEvent);
                            break;
                    }
                }));

        compositeDisposable.add(stompClient.topic(TOPIC_PREFIX + pokerTableId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    String payloadJson = topicMessage.getPayload();
                    Log.i(TAG, "SUBSCRIBE: MESSAGE: " + payloadJson);
                    listener.onMessage(gson.fromJson(payloadJson, ServerMessageDTO.class));
                }, throwable -> {
                    Log.e(TAG, "SUBSCRIBE: Subscription Error", throwable);
                    listener.onSubscribeError(throwable);
                }));

        stompClient.connect(headers);
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public void disconnect() {
        stompClient.disconnect();

        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    // ***************************************************************
    // WebSocket Send Methods
    // ***************************************************************

    public void sendChatMessage(UUID pokerTableId, SendChatMessageDTO dto, SendListener listener) {
        String destination = String.format(SEND_ENDPOINT_PREFIX + SEND_CHAT_MESSAGE, pokerTableId);
        String message = gson.toJson(dto);
        sendMessage(destination, message, listener);
    }

    public void sendPlayerAction(UUID pokerTableId, SendPlayerActionDTO dto, SendListener listener) {
        String destination = String.format(SEND_ENDPOINT_PREFIX + SEND_PLAYER_ACTION, pokerTableId);
        String message = gson.toJson(dto);
        sendMessage(destination, message, listener);
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
