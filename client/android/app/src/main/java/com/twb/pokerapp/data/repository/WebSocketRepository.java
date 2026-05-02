package com.twb.pokerapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.twb.pokerapp.data.database.dao.ServerMessageDAO;
import com.twb.pokerapp.data.database.entities.ServerMessageEntity;
import com.twb.pokerapp.data.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.enumeration.ServerMessageType;
import com.twb.pokerapp.data.websocket.message.server.payload.BettingRoundUpdatedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.ChatMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealCommunityCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealPlayerCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealerDeterminedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.ErrorMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.GameFinishedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerActionedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerDisconnectedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerSubscribedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.RoundFinishedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.ValidationDTO;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

    private String currentTableId;

    @Inject
    public WebSocketRepository(ServerMessageDAO serverMessageDAO, Gson gson) {
        this.serverMessageDAO = serverMessageDAO;
        this.gson = gson;
    }

    public void setTableId(String tableId) {
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
        serverMessageDAO.getMessagesByTableId(tableId)
                .subscribeOn(Schedulers.io())
                .subscribe(entities -> {
                    List<ServerMessageDTO<?>> loadedMessages = new ArrayList<>();
                    for (ServerMessageEntity entity : entities) {
                        loadedMessages.add(convertToDto(entity));
                    }
                    synchronized (internalList) {
                        for (ServerMessageDTO<?> loaded : loadedMessages) {
                            boolean exists = false;
                            for (ServerMessageDTO<?> current : internalList) {
                                if (current.getTimestamp() == loaded.getTimestamp()) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                internalList.add(loaded);
                            }
                        }
                        List<ServerMessageDTO<?>> sorted = new ArrayList<>(internalList);
                        sorted.sort(Comparator.comparingLong(ServerMessageDTO::getTimestamp));
                        _messages.postValue(sorted);
                    }
                }, this::handleError);
    }

    public void handleNewMessage(ServerMessageDTO<?> message) {
        if (currentTableId == null) return;

        // Save to Room
        ServerMessageEntity entity = new ServerMessageEntity(
                currentTableId,
                message.getTimestamp(),
                message.getType().name(),
                gson.toJson(message.getRawPayload())
        );
        
        Schedulers.io().scheduleDirect(() -> serverMessageDAO.insert(entity));

        // Update In-Memory List
        synchronized (internalList) {
            boolean exists = false;
            for (ServerMessageDTO<?> msg : internalList) {
                if (msg.getTimestamp() == message.getTimestamp()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                internalList.add(message);
                List<ServerMessageDTO<?>> sorted = new ArrayList<>(internalList);
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

    private ServerMessageDTO<?> convertToDto(ServerMessageEntity entity) {
        ServerMessageType type = ServerMessageType.valueOf(entity.getMessageType());
        JsonObject rawPayload = gson.fromJson(entity.getPayload(), JsonObject.class);
        ServerMessageDTO<Object> dto = new ServerMessageDTO<>(type, rawPayload, entity.getTimestamp());
        
        Class<?> payloadClass = type.getPayloadClass();
        if (payloadClass != null && rawPayload != null) {
            dto.setPayload(gson.fromJson(rawPayload, payloadClass));
        }
        return dto;
    }
}
