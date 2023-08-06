package com.twb.pokergame.utils.message;

import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessageType;
import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ServerMessageConverter implements MessageConverter {
    private static final Logger logger = LoggerFactory.getLogger(ServerMessageConverter.class);
    private static final String SERVER_MESSAGE_TYPE_KEY = "type";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String PAYLOAD_KEY = "payload";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object fromMessage(Message<?> message, Class<?> targetClass) {
        byte[] payload = (byte[]) message.getPayload();
        JSONObject jsonObject = getJsonObject(payload);
        ServerMessageType messageType = getServerMessageType(jsonObject);
        Class<?> payloadClass = messageType.getPayloadClass();
        long timestamp = getTimestamp(jsonObject);
        Object payloadObject = getPayloadObject(jsonObject, payloadClass);

        return ServerMessageDTO.create(messageType, timestamp, payloadObject);
    }

    private JSONObject getJsonObject(byte[] payload) {
        try {
            return new JSONObject(new String(payload));
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse JSON Object", e);
        }
    }

    private ServerMessageType getServerMessageType(JSONObject jsonObject) {
        if (!jsonObject.has(SERVER_MESSAGE_TYPE_KEY)) {
            throw new IllegalStateException("Expecting Server Message to contain key "
                    + SERVER_MESSAGE_TYPE_KEY + " in response");
        }
        try {
            String messageTypeString = jsonObject.getString(SERVER_MESSAGE_TYPE_KEY);
            return ServerMessageType.valueOf(messageTypeString);
        } catch (JSONException e) {
            throw new RuntimeException("Failed to get message type string", e);
        }
    }

    private long getTimestamp(JSONObject jsonObject) {
        if (!jsonObject.has(TIMESTAMP_KEY)) {
            logger.warn("Expected Server Message to provide " + TIMESTAMP_KEY
                    + " but it is null. Returning from app instead");
            return System.currentTimeMillis();
        }
        try {
            return jsonObject.getLong(TIMESTAMP_KEY);
        } catch (JSONException e) {
            throw new RuntimeException("Failed to get timestamp long", e);
        }
    }

    private Object getPayloadObject(JSONObject jsonObject, Class<?> payloadClass) {
        try {
            JSONObject payloadString = jsonObject.getJSONObject(PAYLOAD_KEY);
            return objectMapper.readValue(payloadString.toString(), payloadClass);
        } catch (IOException | JSONException e) {
            throw new RuntimeException("Failed to parse server message payload", e);
        }
    }

    @Override
    public Message<?> toMessage(Object payload, MessageHeaders headers) {
        throw new NotImplementedException("Not implemented as not necessary. We do not create Message on the server manually");
    }
}
