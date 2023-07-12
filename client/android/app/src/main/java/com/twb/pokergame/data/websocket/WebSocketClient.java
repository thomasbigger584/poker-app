package com.twb.pokergame.data.websocket;

import android.annotation.SuppressLint;
import android.util.Log;

import com.twb.pokergame.data.auth.AuthStateManager;
import com.twb.pokergame.data.websocket.listener.RequestListener;
import com.twb.pokergame.data.websocket.listener.WebSocketLifecycleListener;
import com.twb.pokergame.data.websocket.params.TopicSubscriptionParams;
import com.twb.stomplib.stomp.StompClient;
import com.twb.stomplib.stomp.StompClientFactory;

import java.util.Date;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/*
 * https://github.com/NaikSoftware/StompProtocolAndroid/issues/49
 *
 * To connect adding /websocket to the end of the endpoint url works
 * I.E. Endpoint '/websocket/incident-wall' and append /websocket
 */
@SuppressLint("CheckResult")
@SuppressWarnings("ResultOfMethodCallIgnored")
public class WebSocketClient {
    private static final String TAG = WebSocketClient.class.getSimpleName();
    private final String baseUrl;
    private final AuthStateManager authStateManager;
    private StompClient stompClient;

    public WebSocketClient(String baseUrl,
                           AuthStateManager authStateManager) {
        this.baseUrl = baseUrl;
        this.authStateManager = authStateManager;
    }

    public void connect(String endpoint) {
        if (checkStompClient()) {
            disconnect();
        }
        String url = getConnectionUrl(endpoint);
        String accessToken = authStateManager.getCurrent().getAccessToken();
        this.stompClient = StompClientFactory.createClient(url, accessToken);
    }

    public String getConnectionUrl(String endpoint) {
        return String.format("ws://%s%s", baseUrl, endpoint);
    }

    public void subscribe(TopicSubscriptionParams params) {
        if (!checkStompClient()) {
            throw new RuntimeException("Not yet connected to websocket endpoint");
        }
        WebSocketLifecycleListener listener = params.getListener();
        stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            listener.onOpened(lifecycleEvent);
                            break;
                        case ERROR:
                            Log.e(TAG, "connect: ", lifecycleEvent.getException());
                            listener.onError(lifecycleEvent);
                            break;
                        case CLOSED:
                            listener.onClosed(lifecycleEvent);
                            break;
                    }
                });

        stompClient.topic(params.getTopic())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onMessage);

        stompClient.connect();
    }

    private boolean checkStompClient() {
        return (stompClient != null &&
                !(stompClient.isConnecting() ||
                        stompClient.isConnected()));
    }

    public void sendViaWebSocket(RequestListener listener) {
        if (checkStompClient()) {
            listener.onFailure(new InstantiationException("Client Not Instantiated"));
            return;
        }

        //todo: update for sending requests client-> server NEEDS TESTED
        stompClient.send("/topic/hello-msg-mapping", "Echo STOMP " + new Date())
                .compose(applySchedulers())
                .subscribe(listener::onSuccess, listener::onFailure);
    }

    private CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }
}
