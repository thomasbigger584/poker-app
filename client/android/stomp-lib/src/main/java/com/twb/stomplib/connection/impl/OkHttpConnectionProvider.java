package com.twb.stomplib.connection.impl;

import android.util.Log;

import com.twb.stomplib.event.LifecycleEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OkHttpConnectionProvider extends AbstractConnectionProvider {
    private final String TAG = OkHttpConnectionProvider.class.getSimpleName();
    private final String uri;
    private final Map<String, String> headers;
    private final OkHttpClient okHttpClient;
    private WebSocket websocket;

    OkHttpConnectionProvider(String uri, Map<String, String> headers,
                             OkHttpClient okHttpClient) {
        super();
        this.uri = uri;
        this.headers = headers != null ? headers : new HashMap<>();
        this.okHttpClient = okHttpClient;
    }

    @Override
    public void rawDisconnect() {
        if (websocket != null) {
            websocket.close(1000, "");
        }
    }

    @Override
    void createWebSocketConnection() {
        Request.Builder requestBuilder = new Request.Builder()
                .url(uri);

        addConnectionHeadersToBuilder(requestBuilder);

        websocket = okHttpClient.newWebSocket(requestBuilder.build(), new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        LifecycleEvent openEvent = new LifecycleEvent(LifecycleEvent.EventType.OPENED);

                        TreeMap<String, String> headersAsMap = headersAsMap(response);

                        openEvent.setHandshakeResponseHeaders(headersAsMap);
                        emitLifecycleEvent(openEvent);
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        if (text.equals("\n")) {
                            Log.d(TAG, "RECEIVED HEARTBEAT");
                        } else {
                            emitMessage(text);
                        }
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, ByteString bytes) {
                        emitMessage(bytes.utf8());
                    }

                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                        websocket = null;
                        emitLifecycleEvent(new LifecycleEvent(LifecycleEvent.EventType.CLOSED));
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                        // in OkHttp, a Failure is equivalent to a JWS-Error *and* a JWS-Close
                        emitLifecycleEvent(new LifecycleEvent(LifecycleEvent.EventType.ERROR, new Exception(t)));
                        websocket = null;
                        emitLifecycleEvent(new LifecycleEvent(LifecycleEvent.EventType.CLOSED));
                    }

                    @Override
                    public void onClosing(final WebSocket webSocket, final int code, final String reason) {
                        webSocket.close(code, reason);
                    }
                }
        );
    }

    @Override
    void rawSend(String stompMessage) {
        websocket.send(stompMessage);
    }

    @Override
    Object getSocket() {
        return websocket;
    }

    private TreeMap<String, String> headersAsMap(Response response) {
        TreeMap<String, String> headersAsMap = new TreeMap<>();
        Headers headers = response.headers();
        for (String key : headers.names()) {
            headersAsMap.put(key, headers.get(key));
        }
        return headersAsMap;
    }

    private void addConnectionHeadersToBuilder(Request.Builder requestBuilder) {
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
    }
}
