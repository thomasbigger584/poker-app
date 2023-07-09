package com.twb.pokergame.websocket;

import android.util.Log;

import com.twb.pokergame.rest.RestClient;
import com.twb.stomplib.client.StompClient;
import com.twb.stomplib.client.StompClientFactory;
import com.twb.stomplib.client.StompMessage;
import com.twb.stomplib.event.LifecycleEvent;

import java.util.Date;
import java.util.HashMap;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
 * https://github.com/NaikSoftware/StompProtocolAndroid/issues/49
 *
 * To connect adding /websocket to the end of the endpoint url works
 * I.E. Endpoint '/websocket/incident-wall' and append /websocket
 */
public class WebSocketClient {
    private static final String TAG = WebSocketClient.class.getSimpleName();
    private static final String WEBSOCKET_ENDPOINT = "/poker-app-ws/websocket";
    private static final String WEBSOCKET_TOPIC = "/topic/poker-app-events";

    private static final Object lock = new Object();

    private static WebSocketClient instance;

    private final StompClient stopClient;

    private Disposable mRestPingDisposable;

    private WebSocketClient() {
        String url = getWebSocketUrl(WEBSOCKET_ENDPOINT, RestClient.TOKEN);
        stopClient = StompClientFactory.createClient(url, new HashMap<>());
    }

    public static WebSocketClient getInstance() {
        WebSocketClient instance = WebSocketClient.instance;
        if (instance == null) {
            synchronized (lock) {
                instance = WebSocketClient.instance;
                if (instance == null) {
                    WebSocketClient.instance = instance = new WebSocketClient();
                }
            }
        }
        return instance;
    }

    private static String getWebSocketUrl(String endpointUrl, String accessToken) {
        if (accessToken == null) {
            return String.format("ws://%s:%s%s", RestClient.URL, RestClient.SERVER_PORT, endpointUrl);
        } else {
            return String.format("ws://%s:%s%s?access_token=%s", RestClient.URL, RestClient.SERVER_PORT, endpointUrl, accessToken);
        }
    }

    public void connect(int connectId, WebSocketLifecycleListener listener) {
        stopClient.lifecycle()
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

        String topicUrl = WEBSOCKET_TOPIC;
//        if (connectId > 0) {
//            topicUrl += "/" + connectId;
//        }

        // Receive events
        stopClient.topic(topicUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onMessage);

        stopClient.connect();
    }

    private boolean checkStompClient() {
        return (stopClient != null &&
                !(stopClient.isConnecting() ||
                        stopClient.isConnected()));
    }

    public void sendViaWebSocket(RequestListener listener) {
        if (checkStompClient()) {
            listener.onFailure(new InstantiationException("Client Not Instantiated"));
            return;
        }
        stopClient.send("/topic/hello-msg-mapping", "Echo STOMP " + new Date())
                .compose(applySchedulers())
                .subscribe(listener::onSuccess, listener::onFailure);
    }

    public void sendViaRest(RequestListener listener) {
        mRestPingDisposable = RestClient.getInstance().getServiceRepository()
                .sendRestEcho("Echo REST " + new Date())
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
        if (stopClient != null) {
            stopClient.disconnect();
        }
        if (mRestPingDisposable != null) {
            mRestPingDisposable.dispose();
        }
    }

    public interface RequestListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    public interface WebSocketLifecycleListener {
        void onOpened(LifecycleEvent lifecycleEvent);

        void onError(LifecycleEvent lifecycleEvent);

        void onClosed(LifecycleEvent lifecycleEvent);

        void onMessage(StompMessage stompMessage);
    }
}
