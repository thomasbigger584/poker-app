package com.twb.pokergame.ui.activity.pokergame;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twb.pokergame.BuildConfig;
import com.twb.pokergame.data.auth.AuthStateManager;
import com.twb.pokergame.data.message.WebSocketMessage;
import com.twb.stomplib.dto.StompHeader;
import com.twb.stomplib.stomp.Stomp;
import com.twb.stomplib.stomp.StompClient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class PokerGameViewModel extends ViewModel {
    private static final String TAG = PokerGameViewModel.class.getSimpleName();

    public final MutableLiveData<Throwable> errors = new MutableLiveData<>();
    public final LiveData<WebSocketMessage> messages = new MutableLiveData<>();
    private final AuthStateManager authStateManager;
    private StompClient stompClient;
    private Gson mGson = new GsonBuilder().create();
    private Disposable mRestPingDisposable;
    private CompositeDisposable compositeDisposable;

    @Inject
    public PokerGameViewModel(AuthStateManager authStateManager) {
        this.authStateManager = authStateManager;
    }

    public void connect(String pokerTableId) {
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
                            break;
                        case ERROR:
                            Log.e(TAG, "connect: Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.i(TAG, "connect: Stomp Connection Closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.e(TAG, "connect: Stomp Failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);

        Disposable dispTopic = stompClient.topic("/topic/loops")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    String payloadJson = topicMessage.getPayload();
                    Log.i(TAG, "connect: message recieved: " + payloadJson);
                    onMessage(mGson.fromJson(payloadJson, WebSocketMessage.class));
                }, this::onSubscribeError);

        compositeDisposable.add(dispTopic);

        stompClient.connect(headers);
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private void onMessage(WebSocketMessage message) {
        Log.i(TAG, "Received: " + message);
    }

    private void onSubscribeError(Throwable throwable) {
        Log.e(TAG, "Error on subscribe topic", throwable);
    }

    public void disconnect() {
        stompClient.disconnect();

        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }
}
