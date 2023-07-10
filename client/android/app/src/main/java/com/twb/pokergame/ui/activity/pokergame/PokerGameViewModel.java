package com.twb.pokergame.ui.activity.pokergame;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokergame.data.model.PokerTable;
import com.twb.pokergame.data.websocket.message.PokerAppWebSocketMessage;
import com.twb.pokergame.data.websocket.WebSocketClient;
import com.twb.pokergame.data.websocket.listener.WebSocketLifecycleListener;
import com.twb.pokergame.data.websocket.params.TopicSubscriptionParams;
import com.twb.pokergame.data.websocket.params.WebSocketConnectionParams;
import com.twb.stomplib.event.LifecycleEvent;
import com.twb.stomplib.stomp.StompMessage;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PokerGameViewModel extends ViewModel implements WebSocketLifecycleListener {
    private static final String TAG = PokerGameViewModel.class.getSimpleName();
    private static final String WEBSOCKET_TOPIC = "/topic/poker-app-events.%s";
    private static final String WEBSOCKET_ENDPOINT = "/poker-app-ws/websocket";
    public final MutableLiveData<Throwable> errors = new MutableLiveData<>();
    public final LiveData<PokerAppWebSocketMessage> messages = new MutableLiveData<>();
    private final WebSocketClient client;

    @Inject
    public PokerGameViewModel(WebSocketClient client) {
        this.client = client;
    }

    public void connect() {
        WebSocketConnectionParams params = new WebSocketConnectionParams();
        params.setEndpoint(WEBSOCKET_ENDPOINT);
        params.setToken(null);
        try {
            client.connect(params);
        } catch (Exception e) {
            errors.postValue(e);
        }
    }

    public void subscribe(PokerTable pokerTable) {
        TopicSubscriptionParams params = new TopicSubscriptionParams();
        params.setTopic(String.format(WEBSOCKET_TOPIC, pokerTable.getId()));
        params.setListener(this);
        client.subscribe(params);
    }

    @Override
    public void onOpened(LifecycleEvent event) {
        Log.i(TAG, "onOpened: " + event);
    }

    @Override
    public void onError(LifecycleEvent event) {
        if (event.getException() != null) {
            Log.e(TAG, "onError: " + event, event.getException());
        } else {
            Log.e(TAG, "onError: " + event);
        }
    }

    @Override
    public void onClosed(LifecycleEvent event) {
        Log.i(TAG, "onClosed: " + event);
    }

    @Override
    public void onMessage(StompMessage message) {
        Log.i(TAG, "onMessage: " + message);
    }

    public void disconnect() {
        client.disconnect();
    }
}
