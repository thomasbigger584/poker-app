package com.twb.pokerapp.data.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"tableId", "timestamp"})
public class ServerMessageEntity {

    @NonNull
    private final String tableId;

    private final long timestamp;

    private final String messageType;

    private final String payload;

    public ServerMessageEntity(@NonNull String tableId, long timestamp, String messageType, String payload) {
        this.tableId = tableId;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.payload = payload;
    }

    @NonNull
    public String getTableId() {
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
