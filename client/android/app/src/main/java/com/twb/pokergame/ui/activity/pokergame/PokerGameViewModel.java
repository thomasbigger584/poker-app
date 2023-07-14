package com.twb.pokergame.ui.activity.pokergame;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.twb.pokergame.data.websocket.WebSocketClient;
import com.twb.pokergame.data.websocket.message.client.CreateChatMessageDTO;
import com.twb.pokergame.data.websocket.message.client.PlayerConnectDTO;
import com.twb.pokergame.data.websocket.message.server.ServerMessageDTO;
import com.twb.stomplib.dto.LifecycleEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PokerGameViewModel extends ViewModel implements WebSocketClient.WebSocketListener, WebSocketClient.SendListener {
    private static final String TAG = PokerGameViewModel.class.getSimpleName();
    private final WebSocketClient webSocketClient;

    private String pokerTableId;

    @Inject
    public PokerGameViewModel(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }


    // ***************************************************************
    // WebSocket Lifecycle
    // ***************************************************************

    public void connect(String pokerTableId) {
        this.pokerTableId = pokerTableId;
        webSocketClient.connect(pokerTableId, this);
    }

    @Override
    public void onOpened(LifecycleEvent event) {
        Log.i(TAG, "onOpened: " + event.getMessage());

        // Send Player Connect Message
        PlayerConnectDTO dto = new PlayerConnectDTO();
        webSocketClient.send(pokerTableId, dto, this);
    }

    @Override
    public void onConnectError(LifecycleEvent event) {
        Log.e(TAG, "onConnectError: ", event.getException());
    }

    @Override
    public void onMessage(ServerMessageDTO message) {
        Log.i(TAG, "onMessage: " + message);
    }

    public void sendChatMessage(String pokerTableId, String message) {
        CreateChatMessageDTO dto = new CreateChatMessageDTO();
        dto.setMessage(message);
        webSocketClient.send(pokerTableId, dto, this);
    }

    @Override
    public void onSendSuccess() {
        Log.i(TAG, "onSendSuccess: ");
    }

    @Override
    public void onSendFailure(Throwable throwable) {
        Log.e(TAG, "onSendFailure: ", throwable);
    }

    @Override
    public void onClosed(LifecycleEvent event) {
        Log.i(TAG, "onClosed: ");
    }

    @Override
    public void onFailedServerHeartbeat(LifecycleEvent event) {
        Log.e(TAG, "onFailedServerHeartbeat: " + event.getException());
    }

    @Override
    public void onSubscribeError(Throwable throwable) {
        Log.e(TAG, "onSubscribeError: ", throwable);
    }

    public void disconnect() {
        webSocketClient.disconnect();
    }
}
