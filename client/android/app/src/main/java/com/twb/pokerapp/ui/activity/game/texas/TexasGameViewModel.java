package com.twb.pokerapp.ui.activity.game.texas;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.proto.AppUserDTO;
import com.twb.pokerapp.proto.CreateBotConnectionDTO;
import com.twb.pokerapp.proto.CreateChatMessageDTO;
import com.twb.pokerapp.proto.CreatePlayerActionDTO;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.proto.ServerMessageDTO;
import com.twb.pokerapp.data.repository.AppUserRepository;
import com.twb.pokerapp.data.repository.RepositoryCallback;
import com.twb.pokerapp.data.repository.WebSocketRepository;
import com.twb.pokerapp.data.websocket.WebSocketClient;
import com.twb.pokerapp.util.Protos;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TexasGameViewModel extends ViewModel implements WebSocketClient.SendListener {

    private static final String TAG = TexasGameViewModel.class.getSimpleName();
    private final WebSocketRepository repository;
    private final AppUserRepository appUserRepository;
    private final WebSocketClient webSocketClient;

    public final LiveData<List<ServerMessageDTO>> messages;
    public final LiveData<Throwable> errors;
    public final LiveData<Boolean> connected;

    private UUID tableId;

    @Inject
    public TexasGameViewModel(WebSocketRepository repository,
                              AppUserRepository appUserRepository,
                              WebSocketClient webSocketClient) {
        this.repository = repository;
        this.appUserRepository = appUserRepository;
        this.webSocketClient = webSocketClient;
        this.messages = repository.messages;
        this.errors = repository.errors;
        this.connected = repository.connected;
    }

    public void setTableId(UUID tableId) {
        this.tableId = tableId;
        // Catch up from the persisted log when first observing. The connection lifecycle
        // (and any stale-data clearing) is owned by WebSocketService.
        repository.loadFromDatabase(tableId);
    }

    public void sendChatMessage(String message) {
        if (tableId == null) return;
        var dto = CreateChatMessageDTO.newBuilder()
                .setMessage(message)
                .build();
        webSocketClient.sendChatMessage(tableId, dto, this);
    }

    public void getBots(RepositoryCallback<List<AppUserDTO>> callback) {
        appUserRepository.getBots(callback);
    }

    public void sendBotConnection(String botUserId, Double buyInAmount) {
        var dto = CreateBotConnectionDTO.newBuilder()
                .setBotUserId(botUserId)
                .setBuyInAmount(Protos.moneyStr(buyInAmount == null ? 0d : buyInAmount))
                .build();
        webSocketClient.sendBotConnection(tableId, dto, this);
    }

    public void onPlayerAction(ActionType actionType) {
        onPlayerAction(actionType, null);
    }

    public void onPlayerAction(ActionType actionType, Double amount) {
        if (tableId == null) return;
        var dto = CreatePlayerActionDTO.newBuilder()
                .setAction(actionType)
                .setAmount(amount == null ? "" : Protos.moneyStr(amount))
                .build();
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
