package com.twb.pokergame.websocket;

import android.util.Log;

import com.twb.pokergame.rest.RestClient;
import com.twb.stomplib.LifecycleEvent;
import com.twb.stomplib.Stomp;
import com.twb.stomplib.client.StompClient;
import com.twb.stomplib.client.StompMessage;

import java.util.Date;

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

    private static final String WEBSOCKET_ENDPOINT = "/websocket/tracker/websocket";
    private static final String WEBSOCKET_TOPIC = "/topic/venue";

    private static final Object lock = new Object();

    private static WebSocketClient instance;

    private StompClient mStompClient;

    private Disposable mRestPingDisposable;

    private WebSocketClient() {
        String url = getWebSocketUrl(WEBSOCKET_ENDPOINT, RestClient.TOKEN);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);
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
        mStompClient.lifecycle()
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
        if (connectId > 0) {
            topicUrl += "/" + connectId;
        }

        // Receive events
        mStompClient.topic(topicUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener::onMessage);

        mStompClient.connect();
    }

    private boolean checkStompClient() {
        return (mStompClient != null &&
                !(mStompClient.isConnecting() ||
                        mStompClient.isConnected()));
    }

    public void sendViaWebSocket(RequestListener listener) {
        if (checkStompClient()) {
            listener.onFailure(new InstantiationException("Client Not Instantiated"));
            return;
        }
        mStompClient.send("/topic/hello-msg-mapping", "Echo STOMP " + new Date())
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
        if (mStompClient != null) {
            mStompClient.disconnect();
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
