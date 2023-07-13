package com.twb.pokergame.ui.activity.pokergame;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.twb.pokergame.BuildConfig;
import com.twb.pokergame.data.auth.AuthStateManager;
import com.twb.pokergame.data.message.client.CreateChatMessageDTO;
import com.twb.pokergame.data.message.client.PlayerConnectDTO;
import com.twb.pokergame.data.message.server.ServerMessage;
import com.twb.stomplib.dto.LifecycleEvent;
import com.twb.stomplib.dto.StompHeader;
import com.twb.stomplib.stomp.Stomp;
import com.twb.stomplib.stomp.StompClient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class PokerGameViewModel extends ViewModel {
    private static final String TAG = PokerGameViewModel.class.getSimpleName();
    private final AuthStateManager authStateManager;
    private final Gson gson;
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;

    @Inject
    public PokerGameViewModel(AuthStateManager authStateManager, Gson gson) {
        this.authStateManager = authStateManager;
        this.gson = gson;
    }

    public void connect(String pokerTableId, WebSocketListener listener) {
        String accessToken = authStateManager.getCurrent().getAccessToken();
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + BuildConfig.API_BASE_URL + "/looping/websocket");

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("X-Authorization", "Bearer " + accessToken));

        stompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);

        resetSubscriptions();

        Disposable dispLifecycle = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "connect: Stomp Connection Opened");
                            listener.onOpened(lifecycleEvent);
                            break;
                        case ERROR:
                            Log.e(TAG, "connect: Stomp connection error", lifecycleEvent.getException());
                            listener.onConnectError(lifecycleEvent);
                            break;
                        case CLOSED:
                            Log.i(TAG, "connect: Stomp Connection Closed");
                            listener.onClosed(lifecycleEvent);
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.e(TAG, "connect: Stomp Failed server heartbeat");
                            listener.onFailedServerHeartbeat(lifecycleEvent);
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);

        Disposable dispTopic = stompClient.topic("/topic/loops." + pokerTableId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    String payloadJson = topicMessage.getPayload();
                    Log.i(TAG, "connect: message received: " + payloadJson);
                    listener.onMessage(gson.fromJson(payloadJson, ServerMessage.class));
                }, throwable -> {
                    Log.e(TAG, "connect: subscription error", throwable);
                    listener.onSubscribeError(throwable);
                });

        compositeDisposable.add(dispTopic);

        stompClient.connect(headers);
    }

    public void send(String pokerTableId, CreateChatMessageDTO message, SendListener listener) {
        String jsonMessage = gson.toJson(message);
        String destination = String.format("/app/pokerTable/%s/sendChatMessage", pokerTableId);
        compositeDisposable.add(stompClient.send(destination, jsonMessage)
                .compose(applySchedulers())
                .subscribe(listener::onSuccess, listener::onFailure));
    }

    public void send(String pokerTableId, PlayerConnectDTO message, SendListener listener) {
        String jsonMessage = gson.toJson(message);
        String destination = String.format("/app/pokerTable/%s/sendConnectPlayer", pokerTableId);
        compositeDisposable.add(stompClient.send(destination, jsonMessage)
                .compose(applySchedulers())
                .subscribe(listener::onSuccess, listener::onFailure));
    }

    private CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    @MainThread
    public interface SendListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    @MainThread
    public interface WebSocketListener {
        void onOpened(LifecycleEvent event);

        void onConnectError(LifecycleEvent event);

        void onClosed(LifecycleEvent event);

        void onFailedServerHeartbeat(LifecycleEvent event);

        void onMessage(ServerMessage message);

        void onSubscribeError(Throwable throwable);
    }
}
