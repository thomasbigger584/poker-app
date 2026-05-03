package com.twb.pokerapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.twb.pokerapp.data.database.dao.ServerMessageDAO;
import com.twb.pokerapp.data.database.entities.ServerMessageEntity;
import com.twb.pokerapp.data.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.enumeration.ServerMessageType;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.schedulers.Schedulers;

@Singleton
public class WebSocketRepository {
    private final ServerMessageDAO serverMessageDAO;
    private final Gson gson;

    private final List<ServerMessageDTO<?>> internalList = new CopyOnWriteArrayList<>();
    private final MutableLiveData<List<ServerMessageDTO<?>>> _messages = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<ServerMessageDTO<?>>> messages = _messages;

    private final MutableLiveData<Throwable> _errors = new MutableLiveData<>();
    public final LiveData<Throwable> errors = _errors;

    private final MutableLiveData<Boolean> _connected = new MutableLiveData<>(false);
    public final LiveData<Boolean> connected = _connected;

    private UUID currentTableId;

    private PlayerTurnDTO currentPlayerTurn;
    private long currentPlayerTurnTimestamp;

    @Inject
    public WebSocketRepository(ServerMessageDAO serverMessageDAO, Gson gson) {
        this.serverMessageDAO = serverMessageDAO;
        this.gson = gson;
    }

    public void setTableId(UUID tableId) {
        if (tableId == null) {
            this.currentTableId = null;
            this.internalList.clear();
            this._messages.postValue(new ArrayList<>());
            this._connected.postValue(false);
            return;
        }
        if (tableId.equals(this.currentTableId)) {
            return;
        }
        this.currentTableId = tableId;
        this.internalList.clear();
        this._messages.postValue(new ArrayList<>(internalList));

        // Load existing messages from DB for "catch-up"
        var ignored = serverMessageDAO.getMessagesByTableId(tableId)
                .subscribeOn(Schedulers.io())
                .subscribe(entities -> {
                    var loadedMessages = new ArrayList<ServerMessageDTO<?>>();
                    for (var entity : entities) {
                        loadedMessages.add(convertToDto(entity));
                    }
                    synchronized (internalList) {
                        for (var loaded : loadedMessages) {
                            var exists = false;
                            for (var current : internalList) {
                                if (current.getTimestamp() == loaded.getTimestamp()) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                internalList.add(loaded);
                            }
                        }
                        var sorted = new ArrayList<>(internalList);
                        sorted.sort(Comparator.comparingLong(ServerMessageDTO::getTimestamp));
                        _messages.postValue(sorted);
                    }
                }, this::handleError);
    }

    public void handleNewMessage(ServerMessageDTO<?> message) {
        if (currentTableId == null) return;

        if (message.getType() == ServerMessageType.PLAYER_TURN) {
            currentPlayerTurn = (PlayerTurnDTO) message.getPayload();
            currentPlayerTurnTimestamp = message.getTimestamp();
        } else if (message.getType().isTurnEndingMessage()) {
            currentPlayerTurn = null;
            currentPlayerTurnTimestamp = 0L;
        }

        // Save to Room
        var entity = new ServerMessageEntity(
                currentTableId,
                message.getTimestamp(),
                message.getType().name(),
                gson.toJson(message.getRawPayload())
        );

        Schedulers.io().scheduleDirect(() -> serverMessageDAO.insert(entity));

        // Update In-Memory List
        synchronized (internalList) {
            var exists = false;
            for (var msg : internalList) {
                if (msg.getTimestamp() == message.getTimestamp()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                internalList.add(message);
                var sorted = new ArrayList<>(internalList);
                sorted.sort(Comparator.comparingLong(ServerMessageDTO::getTimestamp));
                _messages.postValue(sorted);
            }
        }
    }

    public void handleError(Throwable throwable) {
        _errors.postValue(throwable);
    }

    public void setConnected(boolean connected) {
        _connected.postValue(connected);
    }

    public PlayerTurnDTO getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public long getCurrentPlayerTurnTimestamp() {
        return currentPlayerTurnTimestamp;
    }

    private ServerMessageDTO<?> convertToDto(ServerMessageEntity entity) {
        var type = ServerMessageType.valueOf(entity.getMessageType());
        var rawPayload = gson.fromJson(entity.getPayload(), JsonObject.class);
        var dto = new ServerMessageDTO<>(type, rawPayload, entity.getTimestamp());

        var payloadClass = type.getPayloadClass();
        if (payloadClass != null && rawPayload != null) {
            dto.setPayload(gson.fromJson(rawPayload, payloadClass));
        }
        return dto;
    }
}
