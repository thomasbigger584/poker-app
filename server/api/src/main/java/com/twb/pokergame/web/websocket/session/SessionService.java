package com.twb.pokergame.web.websocket.session;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Component
public class SessionService {
    private static final String SESSION_POKER_TABLE_ID = "SESSION_POKER_TABLE_ID";

    public void putPokerTableId(StompHeaderAccessor headerAccessor, UUID pokerTableId) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            sessionAttributes = new HashMap<>();
        }
        sessionAttributes.put(SESSION_POKER_TABLE_ID, pokerTableId);
        headerAccessor.setSessionAttributes(sessionAttributes);
    }

    public Optional<UUID> getPokerTableId(StompHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null || !sessionAttributes.containsKey(SESSION_POKER_TABLE_ID)) {
            return Optional.empty();
        }
        return Optional.of((UUID) sessionAttributes.get(SESSION_POKER_TABLE_ID));
    }
}
