package com.twb.stomplib.connection.impl;

import com.twb.stomplib.dto.LifecycleEvent;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.annotations.Nullable;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OkHttpConnectionProvider extends AbstractConnectionProvider {
    public static final String TAG = "OkHttpConnProvider";

    private final String mUri;
    @NotNull
    private final Map<String, String> mConnectHttpHeaders;
    private final OkHttpClient mOkHttpClient;

    @Nullable
    private WebSocket openSocket;

    public OkHttpConnectionProvider(String uri, @Nullable Map<String, String> connectHttpHeaders, OkHttpClient okHttpClient) {
        super();
        mUri = uri;
        mConnectHttpHeaders = connectHttpHeaders != null ? connectHttpHeaders : new HashMap<>();
        mOkHttpClient = okHttpClient;
    }

    @Override
    public void rawDisconnect() {
        if (openSocket != null) {
            openSocket.close(1000, "");
        }
    }

    @Override
    protected void createWebSocketConnection() {
        var requestBuilder = new Request.Builder().url(mUri);

        addConnectionHeadersToBuilder(requestBuilder, mConnectHttpHeaders);

        openSocket = mOkHttpClient.newWebSocket(requestBuilder.build(),
                new WebSocketListener() {
                    @Override
                    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                        var openEvent = new LifecycleEvent(LifecycleEvent.Type.OPENED);
                        var headersAsMap = headersAsMap(response);

                        openEvent.setHandshakeResponseHeaders(headersAsMap);
                        emitLifecycleEvent(openEvent);
                    }

                    @Override
                    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                        emitMessage(text);
                    }

                    @Override
                    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                        emitMessage(bytes.utf8());
                    }

                    @Override
                    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                        openSocket = null;
                        emitLifecycleEvent(new LifecycleEvent(LifecycleEvent.Type.CLOSED));
                    }

                    @Override
                    public void onFailure(@NotNull WebSocket webSocket, Throwable t, Response response) {
                        // in OkHttp, a Failure is equivalent to a JWS-Error *and* a JWS-Close
                        emitLifecycleEvent(new LifecycleEvent(LifecycleEvent.Type.ERROR, new Exception(t)));
                        openSocket = null;
                        emitLifecycleEvent(new LifecycleEvent(LifecycleEvent.Type.CLOSED));
                    }

                    @Override
                    public void onClosing(@NotNull final WebSocket webSocket, final int code, @NotNull final String reason) {
                        webSocket.close(code, reason);
                    }
                }

        );
    }

    @Override
    protected void rawSend(String stompMessage) {
        openSocket.send(stompMessage);
    }

    @Nullable
    @Override
    protected Object getSocket() {
        return openSocket;
    }

    @NotNull
    private TreeMap<String, String> headersAsMap(@NotNull Response response) {
        var headersAsMap = new TreeMap<String, String>();
        var headers = response.headers();
        for (var key : headers.names()) {
            headersAsMap.put(key, headers.get(key));
        }
        return headersAsMap;
    }

    private void addConnectionHeadersToBuilder(@NotNull Request.Builder requestBuilder, @NotNull Map<String, String> mConnectHttpHeaders) {
        for (var headerEntry : mConnectHttpHeaders.entrySet()) {
            requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
    }
}
