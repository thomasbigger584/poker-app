package com.twb.pokergame.web.websocket.session;

import com.twb.pokergame.web.websocket.PokerTableWebSocketController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionEventListener {
    private static final Logger logger = LoggerFactory.getLogger(SessionEventListener.class);
    private final SessionService sessionService;
    private final PokerTableWebSocketController webSocketController;

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        logger.info("Received a new web socket subscription + " + new String(event.getMessage().getPayload()));
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection + " + new String(event.getMessage().getPayload()));
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal == null) {
            logger.warn("Session disconnect cannot disconnect player as principal is null");
            return;
        }
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Optional<UUID> tableIdOpt = sessionService.getPokerTableId(headerAccessor);
        if (tableIdOpt.isEmpty()) {
            logger.warn("Session disconnect cannot disconnect player as no poker table id found on session");
            return;
        }
        webSocketController.sendDisconnectPlayer(principal, tableIdOpt.get());
    }
}
