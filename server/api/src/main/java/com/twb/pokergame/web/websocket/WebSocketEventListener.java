package com.twb.pokergame.web.websocket;

import com.twb.pokergame.service.game.PokerGameThreadHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Optional;

// Generic websocket connect/disconnect. This gets called when the session is created
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SessionService sessionService;
    private final PokerGameThreadHandler threadHandler;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("WebSocketChatEventListener.handleWebSocketConnectListener");
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal == null) {
            logger.warn("Session disconnect cannot disconnect player as principal is null");
            return;
        }
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Optional<String> pokerTableIdOpt = sessionService.getPokerTableId(headerAccessor);
        if (pokerTableIdOpt.isEmpty()) {
            logger.warn("Session disconnect cannot disconnect player as no poker table id found on session");
            return;
        }
        threadHandler.onPlayerDisconnected(pokerTableIdOpt.get(), principal.getName());
    }
}
