package com.twb.stomplib.stomp;

import android.annotation.SuppressLint;
import android.util.Log;

import com.twb.stomplib.connection.ConnectionProvider;
import com.twb.stomplib.event.LifecycleEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

@SuppressLint("CheckResult")
@SuppressWarnings("ResultOfMethodCallIgnored")
public class StompClient {
    public static final String SUPPORTED_VERSIONS = "1.1,1.0";
    public static final String DEFAULT_ACK = "auto";
    private static final String TAG = StompClient.class.getSimpleName();
    private final ConnectionProvider connectionProvider;
    private final BehaviorSubject<Boolean> connectionStream;
    private final PublishSubject<StompMessage> messageStream;
    private final ConcurrentHashMap<String, Flowable<StompMessage>> streamMap;
    private ConcurrentHashMap<String, String> topics;
    private boolean connected;
    private boolean connecting;
    private boolean legacyWhitespace;
    private Parser parser;
    private Disposable lifecycleDisposable;
    private Disposable messagesDisposable;
    private List<StompHeader> headers;
    private int heartbeat;

    public StompClient(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        this.messageStream = PublishSubject.create();
        this.streamMap = new ConcurrentHashMap<>();
        this.connectionStream = BehaviorSubject.createDefault(false);
        this.parser = Parser.NONE;
    }

    /**
     * Set the wildcard parser for Topic subscription.
     * <p>
     * Right now, the only options are NONE and RABBITMQ.
     * <p>
     * When set to RABBITMQ, topic subscription allows for RMQ-style wildcards.
     * <p>
     * See more info <a href="https://www.rabbitmq.com/tutorials/tutorial-five-java.html">here</a>.
     *
     * @param parser Set to NONE by default
     */
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    /**
     * Sets the heartbeat interval to request from the server.
     * <p>
     * Not very useful yet, because we don't have any heartbeat logic on our side.
     *
     * @param ms heartbeat time in milliseconds
     */
    public void setHeartbeat(int ms) {
        heartbeat = ms;
        connectionProvider.setHeartbeat(ms).subscribe();
    }

    /**
     * Connect without reconnect if connected
     */
    public void connect() {
        connect(null);
    }

    /**
     * Connect to websocket. If already connected, this will silently fail.
     *
     * @param headers HTTP headers to send in the INITIAL REQUEST, i.e. during the protocol upgrade
     */
    public void connect(List<StompHeader> headers) {
        if (connected) {
            return;
        }

        this.headers = headers;
        lifecycleDisposable = connectionProvider.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            List<StompHeader> stompHeaders = new ArrayList<>();
                            stompHeaders.add(new StompHeader(StompHeader.VERSION, SUPPORTED_VERSIONS));
                            stompHeaders.add(new StompHeader(StompHeader.HEART_BEAT, "0," + heartbeat));
                            if (this.headers != null) {
                                stompHeaders.addAll(this.headers);
                            }

                            StompMessage stompMessage = new StompMessage(StompCommand.CONNECT, stompHeaders, null);
                            String messageStr = stompMessage.compile(legacyWhitespace);
                            connectionProvider.send(messageStr).subscribe();
                            break;
                        case CLOSED:
                            setConnected(false);
                            connecting = false;
                            break;
                        case ERROR:
                            setConnected(false);
                            connecting = false;
                            break;
                    }
                });

        connecting = true;
        messagesDisposable = connectionProvider.messages()
                .map(StompMessage::from)
                .doOnNext(this::callSubscribers)
                .filter(message -> message.getCommand().equals(StompCommand.CONNECTED))
                .subscribe(stompMessage -> {
                    setConnected(true);
                    connecting = false;
                });
    }

    /**
     * Disconnect from server, and then reconnect with the last-used headers
     */
    public void reconnect() {
        disconnectCompletable().subscribe(() -> connect(headers), exception -> Log.e(TAG, "Disconnect error", exception));
    }

    public Completable send(String destination) {
        return send(destination, null);
    }

    public Completable send(String destination, String data) {
        List<StompHeader> headers = Collections.singletonList(new StompHeader(StompHeader.DESTINATION, destination));
        StompMessage message = new StompMessage(StompCommand.SEND, headers, data);
        return send(message);
    }

    public Completable send(StompMessage stompMessage) {
        String messageStr = stompMessage.compile(legacyWhitespace);
        Completable completable = connectionProvider.send(messageStr);
        CompletableSource connectionComplete = connectionStream
                .filter(isConnected -> isConnected)
                .firstOrError().ignoreElement();
        return completable.startWith(connectionComplete);
    }

    private void callSubscribers(StompMessage stompMessage) {
        messageStream.onNext(stompMessage);
    }

    public Flowable<LifecycleEvent> lifecycle() {
        return connectionProvider.lifecycle().toFlowable(BackpressureStrategy.BUFFER);
    }


    public void disconnect() {
        disconnectCompletable().subscribe(() -> {
        }, exception -> Log.e(TAG, "Disconnect error", exception));
    }

    public Completable disconnectCompletable() {
        if (lifecycleDisposable != null) {
            lifecycleDisposable.dispose();
        }
        if (messagesDisposable != null) {
            messagesDisposable.dispose();
        }
        return connectionProvider.disconnect()
                .doOnComplete(() -> setConnected(false));
    }

    public Flowable<StompMessage> topic(String destinationPath) {
        return topic(destinationPath, null);
    }

    public Flowable<StompMessage> topic(String destPath, List<StompHeader> headerList) {
        if (!streamMap.containsKey(destPath)) {
            streamMap.put(destPath, messageStream
                    .filter(msg -> matches(destPath, msg))
                    .toFlowable(BackpressureStrategy.BUFFER)
                    .doOnSubscribe(disposable -> subscribePath(destPath, headerList).subscribe())
                    .doFinally(() -> unsubscribePath(destPath).subscribe())
                    .share());
        }
        return streamMap.get(destPath);
    }

    /**
     * Reverts to the old frame formatting, which included two newlines between the message body
     * and the end-of-frame marker.
     * <p>
     * Legacy: Body\n\n^@
     * <p>
     * Default: Body^@
     *
     * @param legacyWhitespace whether to append an extra two newlines
     * @see <a href="http://stomp.github.io/stomp-specification-1.2.html#STOMP_Frames">The STOMP spec</a>
     */
    public void setLegacyWhitespace(boolean legacyWhitespace) {
        this.legacyWhitespace = legacyWhitespace;
    }


    //todo: this may be greatly simplified as we only use NONE
    private boolean matches(String path, StompMessage msg) {
        String dest = msg.findHeader(StompHeader.DESTINATION);
        if (dest == null) {
            return false;
        }

        boolean ret;

        switch (parser) {
            case NONE:
                ret = path.equals(dest);
                break;

            case RABBITMQ:
                // for example string "lorem.ipsum.*.sit":

                // split it up into ["lorem", "ipsum", "*", "sit"]
                String[] split = path.split("\\.");
                ArrayList<String> transformed = new ArrayList<>();
                // check for wildcards and replace with corresponding regex
                for (String s : split) {
                    switch (s) {
                        case "*":
                            transformed.add("[^.]+");
                            break;
                        case "#":
                            // TODO: make this work with zero-word
                            // e.g. "lorem.#.dolor" should ideally match "lorem.dolor"
                            transformed.add(".*");
                            break;
                        default:
                            transformed.add(s);
                            break;
                    }
                }
                // at this point, 'transformed' looks like ["lorem", "ipsum", "[^.]+", "sit"]
                StringBuilder sb = new StringBuilder();
                for (String s : transformed) {
                    if (sb.length() > 0) sb.append("\\.");
                    sb.append(s);
                }
                String join = sb.toString();
                // join = "lorem\.ipsum\.[^.]+\.sit"

                ret = dest.matches(join);
                break;

            default:
                ret = false;
                break;
        }

        return ret;
    }

    private Completable subscribePath(String destinationPath, List<StompHeader> headerList) {
        String topicId = UUID.randomUUID().toString();

        if (topics == null) {
            topics = new ConcurrentHashMap<>();
        }

        // Only continue if we don't already have a subscription to the topic
        if (topics.containsKey(destinationPath)) {
            Log.d(TAG, "Attempted to subscribe to already-subscribed path!");
            return Completable.complete();
        }

        topics.put(destinationPath, topicId);
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader(StompHeader.ID, topicId));
        headers.add(new StompHeader(StompHeader.DESTINATION, destinationPath));
        headers.add(new StompHeader(StompHeader.ACK, DEFAULT_ACK));
        if (headerList != null) {
            headers.addAll(headerList);
        }

        return send(new StompMessage(StompCommand.SUBSCRIBE, headers));
    }

    private Completable unsubscribePath(String dest) {
        streamMap.remove(dest);

        String topicId = topics.get(dest);
        topics.remove(dest);

        Log.d(TAG, "Unsubscribe path: " + dest + " id: " + topicId);

        List<StompHeader> headers = Collections.singletonList(new StompHeader(StompHeader.ID, topicId));
        return send(new StompMessage(StompCommand.UNSUBSCRIBE, headers));
    }

    public boolean isConnected() {
        return connected;
    }

    private void setConnected(boolean connected) {
        this.connected = connected;
        connectionStream.onNext(this.connected);
    }

    public boolean isConnecting() {
        return connecting;
    }

    public enum Parser {
        NONE,
        RABBITMQ
    }
}
