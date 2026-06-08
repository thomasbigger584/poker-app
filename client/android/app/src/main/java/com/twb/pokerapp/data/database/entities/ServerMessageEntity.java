package com.twb.pokerapp.data.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(
        tableName = "server_message",
        primaryKeys = {"tableId", "timestamp"}
)
public class ServerMessageEntity {

    @NonNull
    private final UUID tableId;

    private final long timestamp;

    @NonNull
    private final String messageType;

    private final String payload;

    public ServerMessageEntity(
            @NonNull UUID tableId,
            long timestamp,
            @NotNull String messageType,
            String payload
    ) {
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

    @NonNull
    public String getMessageType() {
        return messageType;
    }

    public String getPayload() {
        return payload;
    }
}
