package com.twb.pokergame.web.websocket.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);
    //todo: change this topic to a more meaningful name
    private static final String TOPIC = "/topic/loops.%s";

    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper;

    public void send(UUID pokerTableId, ServerMessageDTO message) {
        try {
            String destination = String.format(TOPIC, pokerTableId);
            String payload = objectMapper.writeValueAsString(message);
            template.convertAndSend(destination, payload);
            logger.info("<<<< " + payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
