package com.twb.pokerapp.data.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.util.UUID;

/**
 * Persisted server message. The payload is the full binary-protobuf {@code ServerMessageDTO}
 * envelope (with its re-based timestamp), so a single column round-trips the entire message without
 * a type discriminator.
 */
@Entity(
        tableName = "server_message",
        primaryKeys = {"tableId", "timestamp"}
)
public class ServerMessageEntity {

    @NonNull
    private final UUID tableId;

    private final long timestamp;

    @NonNull
    private final byte[] payload;

    public ServerMessageEntity(
            @NonNull UUID tableId,
            long timestamp,
            @NonNull byte[] payload
    ) {
        this.tableId = tableId;
        this.timestamp = timestamp;
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
    public byte[] getPayload() {
        return payload;
    }
}
