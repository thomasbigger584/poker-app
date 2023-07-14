package com.twb.pokergame.ui.activity.pokergame;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.twb.pokergame.data.websocket.WebSocketClient;
import com.twb.pokergame.data.websocket.message.client.SendChatMessageDTO;
import com.twb.pokergame.data.websocket.message.client.SendPlayerConnectDTO;
import com.twb.pokergame.data.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.ChatMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerDisconnectedDTO;
import com.twb.stomplib.dto.LifecycleEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PokerGameViewModel extends ViewModel
        implements WebSocketClient.WebSocketListener, WebSocketClient.SendListener {

    private static final String TAG = PokerGameViewModel.class.getSimpleName();
    private final WebSocketClient webSocketClient;
    private final Gson gson;

    private String pokerTableId;

    @Inject
    public PokerGameViewModel(WebSocketClient webSocketClient, Gson gson) {
        this.webSocketClient = webSocketClient;
        this.gson = gson;
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
        // Send Player Connect Message
        SendPlayerConnectDTO dto = new SendPlayerConnectDTO();
        webSocketClient.send(pokerTableId, dto, this);
    }

    @Override
    public void onConnectError(LifecycleEvent event) {
        Log.e(TAG, "onConnectError: ", event.getException());
    }

    @Override
    public void onMessage(ServerMessageDTO<?> message) {
        switch (message.getType()) {
            case PLAYER_CONNECTED: {
                PlayerConnectedDTO dto = (PlayerConnectedDTO) message.getPayload();
                Log.i(TAG, "onMessage: " + dto);
                break;
            }
            case CHAT: {
                ChatMessageDTO dto = (ChatMessageDTO) message.getPayload();
                Log.i(TAG, "onMessage: " + dto);
                break;
            }
            case LOG: {
                LogMessageDTO dto = (LogMessageDTO) message.getPayload();
                Log.i(TAG, "onMessage: " + dto);
                break;
            }
            case PLAYER_DISCONNECTED: {
                PlayerDisconnectedDTO dto = (PlayerDisconnectedDTO) message.getPayload();
                Log.i(TAG, "onMessage: " + dto);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected Game Event Value: " + message.getType());
        }
    }

    public void sendChatMessage(String pokerTableId, String message) {
        SendChatMessageDTO dto = new SendChatMessageDTO();
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
