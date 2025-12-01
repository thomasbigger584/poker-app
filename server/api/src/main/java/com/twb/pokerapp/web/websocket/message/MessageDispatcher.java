package com.twb.pokerapp.web.websocket.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageDispatcher {
    private static final String GAME_TOPIC = "/topic/loops.%s";
    private static final String USER_NOTIFICATION_TOPIC = "/notifications";

    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper;

    public void send(PokerTable table, ServerMessageDTO message) {
        send(table.getId(), message);
    }

    public void send(PlayerSession playerSession, ServerMessageDTO message) {
        var username = playerSession.getUser().getUsername();
        send(username, message);
    }

    public void send(UUID tableId, ServerMessageDTO message) {
        try {
            var destination = String.format(GAME_TOPIC, tableId);
            var payload = objectMapper.writeValueAsString(message);
            template.convertAndSend(destination, payload);
            log.info("<<<< {}", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public void send(String username, ServerMessageDTO message) {
        try {
            var payload = objectMapper.writeValueAsString(message);
            template.convertAndSendToUser(username, USER_NOTIFICATION_TOPIC, payload);
            log.info("<<<< [{}] {}", username, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
