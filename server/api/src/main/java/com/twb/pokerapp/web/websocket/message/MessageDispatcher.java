package com.twb.pokerapp.web.websocket.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
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
    private static final String TOPIC = "/topic/loops.%s";

    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper;

    public void send(PokerTable pokerTable, ServerMessageDTO message) {
        send(pokerTable.getId(), message);
    }

    public void send(PokerTable pokerTable, PlayerSession playerSession, ServerMessageDTO message) {
        var username = playerSession.getUser().getUsername();
        send(pokerTable.getId(), username, message);
    }

    public void send(UUID tableId, ServerMessageDTO message) {
        try {
            String destination = String.format(TOPIC, tableId);
            String payload = objectMapper.writeValueAsString(message);
            template.convertAndSend(destination, payload);
            logger.info("<<<< {}", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public void send(UUID tableId, String username, ServerMessageDTO message) {
        try {
            String destination = String.format(TOPIC, tableId);
            String payload = objectMapper.writeValueAsString(message);
            template.convertAndSendToUser(username, destination, payload);
            logger.info("<<<< [{}] {}", username, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
