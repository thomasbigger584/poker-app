package com.twb.pokerapp.data.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.util.UUID;

@Entity(primaryKeys = {"tableId", "timestamp"})
public class ServerMessageEntity {

    @NonNull
    private final UUID tableId;

    private final long timestamp;

    private final String messageType;

    private final String payload;

    public ServerMessageEntity(
            @NonNull UUID tableId, long timestamp, String messageType, String payload) {
        this.tableId = tableId;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.payload = payload;
    }

    @NonNull
    public UUID getTableId() {
        return tableId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getPayload() {
        return payload;
    }
}
