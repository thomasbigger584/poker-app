package com.twb.pokergame.data.retrofit.gson;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.twb.pokergame.data.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.data.websocket.message.server.enumeration.ServerMessageType;
import com.twb.pokergame.data.websocket.message.server.payload.ChatMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerDisconnectedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerSubscribedDTO;

import java.lang.reflect.Type;

public class ServerMessageDeserializer implements JsonDeserializer<ServerMessageDTO<?>> {
    private static final String TAG = ServerMessageDeserializer.class.getSimpleName();
    private static final String SERVER_MESSAGE_TYPE_KEY = "type";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String PAYLOAD_KEY = "payload";

    @Override
    public ServerMessageDTO<?> deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        ServerMessageType messageType = getServerMessageType(jsonObject);
        Class<?> payloadClass = getPayloadClass(messageType);
        long timestamp = getTimestamp(jsonObject);

        ServerMessageDTO<?> serverMessageDto = new ServerMessageDTO<>(messageType, jsonObject, timestamp);

        if (jsonObject.has(PAYLOAD_KEY)) {
            JsonObject payloadString = jsonObject.get(PAYLOAD_KEY).getAsJsonObject();
            serverMessageDto.setPayload(context.deserialize(payloadString, payloadClass));
        }

        return serverMessageDto;
    }

    private long getTimestamp(JsonObject jsonObject) {
        if (!jsonObject.has(TIMESTAMP_KEY)) {
            Log.w(TAG, "Expected Server Message to provide " + TIMESTAMP_KEY
                    + " but it is null. Returning from app instead");
            return System.currentTimeMillis();
        }
        return jsonObject.get(TIMESTAMP_KEY).getAsLong();
    }

    @NonNull
    private ServerMessageType getServerMessageType(JsonObject jsonObject) {
        if (!jsonObject.has(SERVER_MESSAGE_TYPE_KEY)) {
            throw new IllegalStateException("Expecting Server Message to contain key "
                    + SERVER_MESSAGE_TYPE_KEY + " in response");
        }
        String messageTypeString = jsonObject.get(SERVER_MESSAGE_TYPE_KEY).getAsString();
        return ServerMessageType.valueOf(messageTypeString);
    }

    private Class<?> getPayloadClass(ServerMessageType messageType) {
        switch (messageType) {
            case PLAYER_SUBSCRIBED:
                return PlayerSubscribedDTO.class;
            case CHAT:
                return ChatMessageDTO.class;
            case LOG:
                return LogMessageDTO.class;
            case PLAYER_DISCONNECTED:
                return PlayerDisconnectedDTO.class;
            default:
                throw new IllegalStateException("Unknown Server Message Type: " + messageType);
        }
    }
}
