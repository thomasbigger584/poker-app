package com.twb.pokerapp.data.websocket.message.server;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.twb.pokerapp.data.websocket.message.server.enumeration.ServerMessageType;

public class ServerMessageDTO<T> {
    private final ServerMessageType type;
    private final JsonObject rawPayload;
    private final long timestamp;
    private T payload;

    public ServerMessageDTO(ServerMessageType type,
                            JsonObject rawPayload, long timestamp) {
        this.type = type;
        this.rawPayload = rawPayload;
        this.timestamp = timestamp;
    }

    public ServerMessageType getType() {
        return type;
    }

    public JsonObject getRawPayload() {
        return rawPayload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @NonNull
    @Override
    public String toString() {
        return "ServerMessageDTO{" +
                "type=" + type +
                ", rawPayload='" + rawPayload + '\'' +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
}
