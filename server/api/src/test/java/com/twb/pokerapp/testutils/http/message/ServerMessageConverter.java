package com.twb.pokerapp.testutils.http.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ServerMessageConverter implements MessageConverter {
    private static final String SERVER_MESSAGE_TYPE_KEY = "type";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String PAYLOAD_KEY = "payload";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object fromMessage(Message<?> message, @NotNull Class<?> targetClass) {
        var payload = (byte[]) message.getPayload();
        try {
            var rootNode = objectMapper.readTree(payload);
            var typeStr = rootNode.get(SERVER_MESSAGE_TYPE_KEY).asText();
            var messageType = ServerMessageType.valueOf(typeStr);

            var timestamp = System.currentTimeMillis();
            if (rootNode.has(TIMESTAMP_KEY)) {
                timestamp = rootNode.get(TIMESTAMP_KEY).asLong();
            }
            var payloadNode = rootNode.get(PAYLOAD_KEY);
            var payloadObject = objectMapper.treeToValue(payloadNode, messageType.getPayloadClass());
            return ServerMessageDTO.create(messageType, timestamp, payloadObject);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert message", e);
        }
    }

    @Override
    public Message<?> toMessage(@NotNull Object payload, MessageHeaders headers) {
        try {
            var json = objectMapper.writeValueAsString(payload);
            var payloadBytes = json.getBytes(StandardCharsets.UTF_8);
            return new GenericMessage<>(payloadBytes, headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse bytes for payload", e);
        }
    }
}
