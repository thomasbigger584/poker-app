package com.twb.stomplib.connection.impl;

import android.util.Log;

import com.twb.stomplib.connection.ConnectionProvider;
import com.twb.stomplib.event.LifecycleEvent;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * <p>
 * Created because there was a lot of shared code between JWS and OkHttp connection providers.
 */
public abstract class AbstractConnectionProvider implements ConnectionProvider {
    private static final String TAG = AbstractConnectionProvider.class.getSimpleName();

    private final PublishSubject<LifecycleEvent> lifecycleStream;
    private final PublishSubject<String> messagesStream;

    AbstractConnectionProvider() {
        this.lifecycleStream = PublishSubject.create();
        this.messagesStream = PublishSubject.create();
    }

    @Override
    public Observable<String> messages() {
        return messagesStream.startWith(initSocket().toObservable());
    }

    /**
     * Simply close socket.
     * <p>
     * For example:
     * <pre>
     * webSocket.close();
     * </pre>
     */
    abstract void rawDisconnect();

    @Override
    public Completable disconnect() {
        return Completable.fromAction(this::rawDisconnect);
    }

    private Completable initSocket() {
        return Completable.fromAction(this::createWebSocketConnection);
    }

    // Doesn't do anything at all, only here as a stub
    public Completable setHeartbeat(int ms) {
        return Completable.complete();
    }

    /**
     * Most important method: connects to websocket and notifies program of messages.
     * <p>
     * See implementations in OkHttpConnectionProvider and WebSocketsConnectionProvider.
     */
    abstract void createWebSocketConnection();

    @Override
    public Completable send(String stompMessage) {
        return Completable.fromCallable(() -> {
            if (getSocket() == null) {
                throw new IllegalStateException("Not connected yet");
            } else {
                Log.d(TAG, "Send STOMP message: " + stompMessage);
                rawSend(stompMessage);
                return null;
            }
        });
    }

    /**
     * Just a simple message send.
     * <p>
     * For example:
     * <pre>
     * webSocket.send(stompMessage);
     * </pre>
     *
     * @param stompMessage message to send
     */
    abstract void rawSend(String stompMessage);

    /**
     * Get socket object.
     * Used for null checking; this object is expected to be null when the connection is not yet established.
     * <p>
     * For example:
     * <pre>
     * return webSocket;
     * </pre>
     */
    abstract Object getSocket();

    void emitLifecycleEvent(LifecycleEvent lifecycleEvent) {
        Log.d(TAG, "Emit lifecycle event: " + lifecycleEvent.getType().name());
        lifecycleStream.onNext(lifecycleEvent);
    }

    void emitMessage(String stompMessage) {
        Log.d(TAG, "Emit STOMP message: " + stompMessage);
        messagesStream.onNext(stompMessage);
    }

    @Override
    public Observable<LifecycleEvent> lifecycle() {
        return lifecycleStream;
    }
}