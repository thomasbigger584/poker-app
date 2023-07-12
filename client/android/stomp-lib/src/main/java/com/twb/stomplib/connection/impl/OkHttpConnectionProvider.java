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
    private final String url;
    private final Map<String, String> headers;
    private final OkHttpClient okHttpClient;
    private WebSocket websocket;

    public OkHttpConnectionProvider(String url, OkHttpClient okHttpClient) {
        this(url, null, okHttpClient);
    }

    public OkHttpConnectionProvider(String url, Map<String, String> headers, OkHttpClient okHttpClient) {
        super();
        this.url = url;
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
                .url(url);

        addConnectionHeadersToBuilder(requestBuilder);

        Request request = requestBuilder.build();

        websocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        LifecycleEvent openEvent = new LifecycleEvent(LifecycleEvent.EventType.OPENED);
                        openEvent.setResponseHeaders(headersAsMap(response));

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

    private Map<String, String> headersAsMap(Response response) {
        Headers headers = response.headers();

        Map<String, String> headersAsMap = new TreeMap<>();
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