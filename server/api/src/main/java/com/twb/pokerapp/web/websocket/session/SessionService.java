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
    private static final String SESSION_TABLE_ID = "SESSION_TABLE_ID";
    private static final String SESSION_BUYIN_AMOUNT = "SESSION_BUYIN_AMOUNT";

    // *****************************************************************************************
    // PUT Methods
    // *****************************************************************************************

    public void putConnectionType(StompHeaderAccessor headerAccessor, ConnectionType connectionType) {
        put(headerAccessor, SESSION_CONNECTION_TYPE, connectionType);
    }

    public void putPokerTableId(StompHeaderAccessor headerAccessor, UUID tableId) {
        put(headerAccessor, SESSION_TABLE_ID, tableId);
    }

    public void putBuyInAmount(StompHeaderAccessor headerAccessor, Double buyInAmount) {
        put(headerAccessor, SESSION_BUYIN_AMOUNT, buyInAmount);
    }

    // *****************************************************************************************
    // GET Methods
    // *****************************************************************************************

    public Optional<ConnectionType> getConnectionType(StompHeaderAccessor headerAccessor) {
        return get(headerAccessor, SESSION_CONNECTION_TYPE);
    }

    public Optional<UUID> getPokerTableId(StompHeaderAccessor headerAccessor) {
        return get(headerAccessor, SESSION_TABLE_ID);
    }

    public Optional<Double> getBuyInAmount(StompHeaderAccessor headerAccessor) {
        return get(headerAccessor, SESSION_BUYIN_AMOUNT);
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor headerAccessor) {
        var sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            sessionAttributes = new HashMap<>();
        }
        return sessionAttributes;
    }

    private void put(StompHeaderAccessor headerAccessor, String key, Object value) {
        var sessionAttributes = getSessionAttributes(headerAccessor);
        sessionAttributes.put(key, value);
        headerAccessor.setSessionAttributes(sessionAttributes);
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> get(StompHeaderAccessor headerAccessor, String key) {
        var sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null || !sessionAttributes.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of((T) sessionAttributes.get(key));
    }
}
