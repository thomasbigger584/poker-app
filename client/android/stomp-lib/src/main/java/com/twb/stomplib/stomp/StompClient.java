package com.twb.stomplib.stomp;

import android.annotation.SuppressLint;
import android.util.Log;

import com.twb.stomplib.connection.ConnectionProvider;
import com.twb.stomplib.dto.LifecycleEvent;
import com.twb.stomplib.dto.StompCommand;
import com.twb.stomplib.dto.StompHeader;
import com.twb.stomplib.dto.StompMessage;
import com.twb.stomplib.pathmatcher.PathMatcher;
import com.twb.stomplib.pathmatcher.SimplePathMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

@SuppressLint("CheckResult")
@SuppressWarnings("ResultOfMethodCallIgnored")
public class StompClient {

    public static final String SUPPORTED_VERSIONS = "1.1,1.2";
    public static final String DEFAULT_ACK = "auto";
    private static final String TAG = StompClient.class.getSimpleName();
    private final ConnectionProvider connectionProvider;
    private ConcurrentHashMap<String, String> topics;
    private boolean legacyWhitespace;

    private PublishSubject<StompMessage> messageStream;
    private BehaviorSubject<Boolean> connectionStream;
    private final ConcurrentHashMap<String, Flowable<StompMessage>> streamMap;
    private PathMatcher pathMatcher;
    private Disposable lifecycleDisposable;
    private Disposable messagesDisposable;
    private final PublishSubject<LifecycleEvent> lifecyclePublishSubject;
    private List<StompHeader> headers;
    private final HeartBeatTask heartBeatTask;

    public StompClient(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        streamMap = new ConcurrentHashMap<>();
        lifecyclePublishSubject = PublishSubject.create();
        pathMatcher = new SimplePathMatcher();
        heartBeatTask = new HeartBeatTask(this::sendHeartBeat, () -> {
            lifecyclePublishSubject.onNext(new LifecycleEvent(LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT));
        });
    }

    public StompClient withServerHeartbeat(int ms) {
        heartBeatTask.setServerHeartbeat(ms);
        return this;
    }

    public StompClient withClientHeartbeat(int ms) {
        heartBeatTask.setClientHeartbeat(ms);
        return this;
    }

    public void connect() {
        connect(null);
    }

    public void connect(@Nullable List<StompHeader> _headers) {
        Log.d(TAG, "Connect");
        this.headers = _headers;

        if (isConnected()) {
            Log.d(TAG, "Already connected, ignore");
            return;
        }
        lifecycleDisposable = connectionProvider.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            var headers = new ArrayList<StompHeader>();
                            headers.add(new StompHeader(StompHeader.VERSION, SUPPORTED_VERSIONS));
                            headers.add(new StompHeader(StompHeader.HEART_BEAT,
                                    heartBeatTask.getClientHeartbeat() + "," + heartBeatTask.getServerHeartbeat()));

                            if (_headers != null) headers.addAll(_headers);

                            connectionProvider.send(new StompMessage(StompCommand.CONNECT, headers, null).compile(legacyWhitespace))
                                    .subscribe(() -> {
                                        Log.d(TAG, "Publish open");
                                        lifecyclePublishSubject.onNext(lifecycleEvent);
                                    });
                            break;

                        case CLOSED:
                            Log.d(TAG, "Socket closed");
                            disconnect();
                            break;

                        case ERROR:
                            Log.d(TAG, "Socket closed with error");
                            lifecyclePublishSubject.onNext(lifecycleEvent);
                            break;
                    }
                });

        messagesDisposable = connectionProvider.messages()
                .map(StompMessage::from)
                .filter(heartBeatTask::consumeHeartBeat)
                .doOnNext(getMessageStream()::onNext)
                .filter(msg -> msg.getStompCommand().equals(StompCommand.CONNECTED))
                .subscribe(stompMessage -> {
                    getConnectionStream().onNext(true);
                }, onError -> {
                    Log.e(TAG, "Error parsing message", onError);
                });
    }

    synchronized private BehaviorSubject<Boolean> getConnectionStream() {
        if (connectionStream == null || connectionStream.hasComplete()) {
            connectionStream = BehaviorSubject.createDefault(false);
        }
        return connectionStream;
    }

    synchronized private PublishSubject<StompMessage> getMessageStream() {
        if (messageStream == null || messageStream.hasComplete()) {
            messageStream = PublishSubject.create();
        }
        return messageStream;
    }

    /**
     * Exposes a stream of Receipt IDs received from the server.
     * Used by the client to confirm subscriptions or message delivery.
     */
    public Flowable<String> receipts() {
        return getMessageStream()
                .filter(msg -> msg.getStompCommand().equals(StompCommand.RECEIPT))
                .map(msg -> msg.findHeader(StompHeader.RECEIPT_ID)) // Maps to the value of "receipt-id"
                .toFlowable(BackpressureStrategy.BUFFER);
    }

    public Completable send(String destination) {
        return send(destination, null);
    }

    public Completable send(String destination, String data) {
        return send(new StompMessage(
                StompCommand.SEND,
                Collections.singletonList(new StompHeader(StompHeader.DESTINATION, destination)),
                data));
    }

    public Completable send(@NonNull StompMessage stompMessage) {
        var completable = connectionProvider.send(stompMessage.compile(legacyWhitespace));
        var connectionComplete = getConnectionStream()
                .filter(isConnected -> isConnected)
                .firstElement().ignoreElement();
        return completable
                .startWith(connectionComplete);
    }

    @SuppressLint("CheckResult")
    private void sendHeartBeat(@NonNull String pingMessage) {
        var completable = connectionProvider.send(pingMessage);
        var connectionComplete = getConnectionStream()
                .filter(isConnected -> isConnected)
                .firstElement().ignoreElement();
        completable.startWith(connectionComplete)
                .onErrorComplete()
                .subscribe();
    }

    public Flowable<LifecycleEvent> lifecycle() {
        return lifecyclePublishSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    /**
     * Disconnect from server, and then reconnect with the last-used headers
     */
    @SuppressLint("CheckResult")
    public void reconnect() {
        disconnectCompletable()
                .subscribe(() -> connect(headers),
                        e -> Log.e(TAG, "Disconnect error", e));
    }

    @SuppressLint("CheckResult")
    public void disconnect() {
        disconnectCompletable().subscribe(() -> {
        }, e -> Log.e(TAG, "Disconnect error", e));
    }

    public Completable disconnectCompletable() {
        heartBeatTask.shutdown();
        if (lifecycleDisposable != null) {
            lifecycleDisposable.dispose();
        }
        if (messagesDisposable != null) {
            messagesDisposable.dispose();
        }

        return connectionProvider.disconnect()
                .doFinally(() -> {
                    Log.d(TAG, "Stomp disconnected");
                    getConnectionStream().onComplete();
                    getMessageStream().onComplete();
                    lifecyclePublishSubject.onNext(new LifecycleEvent(LifecycleEvent.Type.CLOSED));
                });
    }

    public Flowable<StompMessage> topic(String destinationPath) {
        return topic(destinationPath, null);
    }

    public Flowable<StompMessage> topic(@NonNull String destPath, List<StompHeader> headerList) {
        if (destPath == null)
            return Flowable.error(new IllegalArgumentException("Topic path cannot be null"));
        else if (!streamMap.containsKey(destPath))
            streamMap.put(destPath,
                    Completable.defer(() -> subscribePath(destPath, headerList)).andThen(
                            getMessageStream()
                                    .filter(msg -> pathMatcher.matches(destPath, msg))
                                    .toFlowable(BackpressureStrategy.BUFFER)
                                    .doFinally(() -> unsubscribePath(destPath).subscribe())
                                    .share())
            );
        return streamMap.get(destPath);
    }

    private Completable subscribePath(String destinationPath, @Nullable List<StompHeader> headerList) {
        var topicId = UUID.randomUUID().toString();
        if (topics == null) topics = new ConcurrentHashMap<>();

        if (topics.containsKey(destinationPath)) {
            Log.d(TAG, "Attempted to subscribe to already-subscribed path!");
            return Completable.complete();
        }

        topics.put(destinationPath, topicId);
        var headers = new ArrayList<StompHeader>();
        headers.add(new StompHeader(StompHeader.ID, topicId));
        headers.add(new StompHeader(StompHeader.DESTINATION, destinationPath));
        headers.add(new StompHeader(StompHeader.ACK, DEFAULT_ACK));
        if (headerList != null) headers.addAll(headerList);

        return send(new StompMessage(StompCommand.SUBSCRIBE,
                headers, null))
                .doOnError(throwable -> unsubscribePath(destinationPath).subscribe());
    }

    private Completable unsubscribePath(String dest) {
        streamMap.remove(dest);
        if (topics == null) {
            return Completable.complete();
        }
        var topicId = topics.get(dest);
        if (topicId == null) {
            return Completable.complete();
        }
        topics.remove(dest);
        Log.d(TAG, "Unsubscribe path: " + dest + " id: " + topicId);
        return send(new StompMessage(StompCommand.UNSUBSCRIBE,
                Collections.singletonList(new StompHeader(StompHeader.ID, topicId)), null)).onErrorComplete();
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public boolean isConnected() {
        return getConnectionStream().getValue();
    }

    public void setLegacyWhitespace(boolean legacyWhitespace) {
        this.legacyWhitespace = legacyWhitespace;
    }

    public String getTopicId(String dest) {
        return topics != null ? topics.get(dest) : null;
    }
}
