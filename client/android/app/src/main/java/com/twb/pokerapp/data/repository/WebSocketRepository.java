package com.twb.pokerapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.protobuf.InvalidProtocolBufferException;
import com.twb.pokerapp.data.database.dao.ServerMessageDAO;
import com.twb.pokerapp.data.database.entities.ServerMessageEntity;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.proto.ServerMessageDTO;
import com.twb.pokerapp.util.Protos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Single source of truth for the live game message log. Backed by Room for crash/process-death
 * persistence and mirrored into an in-memory list that drives the UI through LiveData.
 *
 * <p>The service feeds every inbound STOMP message in via {@link #handleNewMessage}; the
 * activity observes {@link #messages} and renders incrementally. Each message is the binary-protobuf
 * {@link ServerMessageDTO} envelope; Room stores its raw bytes.
 */
@Singleton
public class WebSocketRepository {
    private static final String TAG = WebSocketRepository.class.getSimpleName();
    private final ServerMessageDAO serverMessageDAO;

    /*
     * All Room mutations (insert + delete) run on a single thread so a delete issued on a
     * fresh connection can never be overtaken by an insert that was queued just before it
     * (which would resurrect a row from the previous session).
     */
    private final Scheduler dbScheduler = Schedulers.from(Executors.newSingleThreadExecutor());

    private final List<ServerMessageDTO> internalList =
            Collections.synchronizedList(new ArrayList<>());
    private final MutableLiveData<List<ServerMessageDTO>> _messages = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<ServerMessageDTO>> messages = _messages;

    private final MutableLiveData<Throwable> _errors = new MutableLiveData<>();
    public final LiveData<Throwable> errors = _errors;

    private final MutableLiveData<Boolean> _connected = new MutableLiveData<>(false);
    public final LiveData<Boolean> connected = _connected;

    private UUID currentTableId;

    /*
     * The server stamps every message with System.currentTimeMillis(), so bursts of messages
     * (e.g. bettingRoundUpdated + roundFinished dispatched from the same DB commit) routinely
     * share a millisecond. Since (tableId, timestamp) is the Room primary key AND the UI renders
     * strictly-increasing timestamps, colliding timestamps would silently drop messages at both
     * layers. We therefore re-base each incoming timestamp to be strictly greater than the last.
     * Order is preserved (single ordered STOMP stream); only the rare colliding message shifts by
     * a millisecond, which is immaterial to the one timestamp-sensitive consumer (PLAYER_TURN's
     * countdown), whose turns are seconds apart.
     */
    private long lastTimestamp = 0L;

    /*
     * Incremented whenever the session is reset (fresh connect / clear). A slow DB load that was
     * started for a previous session checks this and discards its result instead of clobbering
     * the new session's state.
     */
    private volatile int sessionGeneration = 0;

    private PlayerTurnDTO currentPlayerTurn;
    private long currentPlayerTurnTimestamp;

    @Inject
    public WebSocketRepository(ServerMessageDAO serverMessageDAO) {
        this.serverMessageDAO = serverMessageDAO;
    }

    /**
     * Called by the UI when it starts observing a table. Loads any persisted log from Room so the
     * UI can catch up after process death. No-op when the in-memory log is already warm for this
     * table (the singleton survived a config change / background round-trip).
     */
    public void loadFromDatabase(UUID tableId) {
        if (tableId == null) {
            return;
        }
        synchronized (internalList) {
            if (tableId.equals(currentTableId) && !internalList.isEmpty()) {
                return;
            }
            currentTableId = tableId;
        }

        final int generation = sessionGeneration;
        var ignored = serverMessageDAO.getMessagesByTableId(tableId)
                .subscribeOn(dbScheduler)
                .subscribe(entities -> {
                    synchronized (internalList) {
                        if (generation != sessionGeneration) {
                            return; // a fresh session started while we were loading
                        }
                        internalList.clear();
                        long maxTs = 0L;
                        for (var entity : entities) {
                            var message = parseEntity(entity);
                            if (message == null) {
                                continue;
                            }
                            internalList.add(message);
                            maxTs = Math.max(maxTs, entity.getTimestamp());
                        }
                        lastTimestamp = Math.max(lastTimestamp, maxTs);
                        publish();
                    }
                }, this::handleError);
    }

    /**
     * Called by the service when a brand-new websocket connection is opened. The server replays
     * full state on subscribe, so any persisted log for this table is stale and is discarded.
     */
    public void onConnectionStarted(UUID tableId) {
        resetSession(tableId);
    }

    /**
     * Called by the service on explicit stop / leave-table. Drops the in-memory log and the
     * persisted rows for the table that is being left.
     */
    public void clearSession() {
        resetSession(null);
    }

    private void resetSession(UUID newTableId) {
        UUID tableToPurge;
        synchronized (internalList) {
            sessionGeneration++;
            tableToPurge = currentTableId;
            currentTableId = newTableId;
            internalList.clear();
            lastTimestamp = 0L;
            currentPlayerTurn = null;
            currentPlayerTurnTimestamp = 0L;
            publish();
        }
        _connected.postValue(false);
        _errors.postValue(null);
        // Purge whichever table we are leaving as well as the one we are (re)joining: both logs
        // are stale now that the server is about to replay state from scratch.
        if (tableToPurge != null) {
            deleteTable(tableToPurge);
        }
        if (newTableId != null && !newTableId.equals(tableToPurge)) {
            deleteTable(newTableId);
        }
    }

    private void deleteTable(UUID tableId) {
        dbScheduler.scheduleDirect(() -> serverMessageDAO.deleteByTableId(tableId));
    }

    public void handleNewMessage(ServerMessageDTO message) {
        final UUID tableId;
        final long timestamp;
        final ServerMessageDTO stored;
        synchronized (internalList) {
            if (currentTableId == null) {
                return;
            }
            tableId = currentTableId;

            // Re-base onto a strictly-increasing timeline so distinct messages never collide.
            timestamp = Math.max(message.getTimestamp(), lastTimestamp + 1);
            lastTimestamp = timestamp;

            stored = message.toBuilder().setTimestamp(timestamp).build();

            if (message.getPayloadCase() == ServerMessageDTO.PayloadCase.PLAYER_TURN) {
                currentPlayerTurn = message.getPlayerTurn();
                currentPlayerTurnTimestamp = timestamp;
            } else if (Protos.isTurnEnding(message.getPayloadCase())) {
                currentPlayerTurn = null;
                currentPlayerTurnTimestamp = 0L;
            }

            internalList.add(stored);
            publish();
        }

        // Persist on the serial DB thread.
        var entity = new ServerMessageEntity(tableId, timestamp, stored.toByteArray());
        dbScheduler.scheduleDirect(() -> serverMessageDAO.insert(entity));
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

    private void publish() {
        // Shallow copy so observers see a new list instance and reliably trigger.
        _messages.postValue(new ArrayList<>(internalList));
    }

    private ServerMessageDTO parseEntity(ServerMessageEntity entity) {
        try {
            return ServerMessageDTO.parseFrom(entity.getPayload());
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG, "Dropping unparseable persisted message", e);
            return null;
        }
    }
}
