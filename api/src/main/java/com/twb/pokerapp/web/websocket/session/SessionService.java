package com.twb.pokerapp.web.websocket.session;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Component
public class SessionService {
    private static final String SESSION_CONNECTION_TYPE = "SESSION_CONNECTION_TYPE";
    private static final String SESSION_POKER_TABLE_ID = "SESSION_POKER_TABLE_ID";


    // *****************************************************************************************
    // PUT Methods
    // *****************************************************************************************

    public void putConnectionType(StompHeaderAccessor headerAccessor, ConnectionType connectionType) {
        Map<String, Object> sessionAttributes = getSessionAttributes(headerAccessor);
        sessionAttributes.put(SESSION_CONNECTION_TYPE, connectionType);
        headerAccessor.setSessionAttributes(sessionAttributes);
    }

    public void putPokerTableId(StompHeaderAccessor headerAccessor, UUID tableId) {
        Map<String, Object> sessionAttributes = getSessionAttributes(headerAccessor);
        sessionAttributes.put(SESSION_POKER_TABLE_ID, tableId);
        headerAccessor.setSessionAttributes(sessionAttributes);
    }

    // *****************************************************************************************
    // GET Methods
    // *****************************************************************************************

    public Optional<ConnectionType> getConnectionType(StompHeaderAccessor headerAccessor) {
        return get(headerAccessor, SESSION_CONNECTION_TYPE);
    }

    public Optional<UUID> getPokerTableId(StompHeaderAccessor headerAccessor) {
        return get(headerAccessor, SESSION_POKER_TABLE_ID);
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            sessionAttributes = new HashMap<>();
        }
        return sessionAttributes;
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> get(StompHeaderAccessor headerAccessor, String key) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null || !sessionAttributes.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of((T) sessionAttributes.get(key));
    }
}
