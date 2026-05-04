package com.twb.pokerapp.ui.activity.game.texas;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.data.model.enumeration.ActionType;
import com.twb.pokerapp.data.repository.WebSocketRepository;
import com.twb.pokerapp.data.websocket.WebSocketClient;
import com.twb.pokerapp.data.websocket.message.client.SendChatMessageDTO;
import com.twb.pokerapp.data.websocket.message.client.SendPlayerActionDTO;
import com.twb.pokerapp.data.websocket.message.server.ServerMessageDTO;

import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TexasGameViewModel extends ViewModel implements WebSocketClient.SendListener {

    private static final String TAG = TexasGameViewModel.class.getSimpleName();
    private final WebSocketRepository repository;
    private final WebSocketClient webSocketClient;

    public final LiveData<List<ServerMessageDTO<?>>> messages;
    public final LiveData<Throwable> errors;
    public final LiveData<Boolean> connected;

    private UUID tableId;

    @Inject
    public TexasGameViewModel(WebSocketRepository repository, WebSocketClient webSocketClient) {
        this.repository = repository;
        this.webSocketClient = webSocketClient;
        this.messages = repository.messages;
        this.errors = repository.errors;
        this.connected = repository.connected;
    }

    public void setTableId(UUID tableId) {
        this.tableId = tableId;
        repository.setTableId(tableId);
    }

    public void sendChatMessage(String message) {
        if (tableId == null) return;
        var dto = new SendChatMessageDTO();
        dto.setMessage(message);
        webSocketClient.sendChatMessage(tableId, dto, this);
    }

    public void onPlayerAction(ActionType actionType) {
        onPlayerAction(actionType, null);
    }

    public void onPlayerAction(ActionType actionType, Double amount) {
        if (tableId == null) return;
        var dto = new SendPlayerActionDTO();
        dto.setAction(actionType.name());
        dto.setAmount(amount);
        webSocketClient.sendPlayerAction(tableId, dto, this);
    }

    public PlayerTurnDTO getCurrentPlayerTurn() {
        return repository.getCurrentPlayerTurn();
    }

    public long getCurrentPlayerTurnTimestamp() {
        return repository.getCurrentPlayerTurnTimestamp();
    }

    @Override
    public void onSendSuccess() {
        Log.i(TAG, "onSendSuccess: Message sent successfully");
    }

    @Override
    public void onSendFailure(Throwable throwable) {
        Log.e(TAG, "onSendFailure: Failed to send message", throwable);
        repository.handleError(throwable);
    }
}
