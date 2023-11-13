package com.twb.pokerapp.web.websocket.session;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.web.websocket.PokerTableWebSocketController;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionEventListener {
    private static final Logger logger = LoggerFactory.getLogger(SessionEventListener.class);
    private static final String HEADER_CONNECTION_TYPE = "X-Connection-Type";

    private final SessionService sessionService;
    private final PokerTableWebSocketController webSocketController;

    @EventListener
    public void handleEvent(SessionConnectEvent event) {
        logger.info("Attempting to connect: {}", event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        List<String> connectionTypeHeader = headerAccessor.getNativeHeader(HEADER_CONNECTION_TYPE);
        if (CollectionUtils.isNotEmpty(connectionTypeHeader)) {
            ConnectionType connectionType = ConnectionType.valueOf(connectionTypeHeader.get(0));
            sessionService.putConnectionType(headerAccessor, connectionType);
        } else {
            sessionService.putConnectionType(headerAccessor, ConnectionType.LISTENER);
        }
    }

    @EventListener
    public void handleEvent(SessionConnectedEvent event) {
        logger.info("Connected: {}", event);
    }

    @EventListener
    public void handleEvent(SessionSubscribeEvent event) {
        logger.info("New Subscription: {}", event);
    }

    @EventListener
    public void handleEvent(SessionUnsubscribeEvent event) {
        logger.info("Un-subscription: {}", event);
    }

    @EventListener
    public void handleEvent(SessionDisconnectEvent event) {
        logger.info("Disconnecting: {}", event);
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
