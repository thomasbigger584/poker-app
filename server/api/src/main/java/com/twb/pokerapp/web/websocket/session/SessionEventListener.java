package com.twb.pokerapp.web.websocket.session;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.web.websocket.TableWebSocketController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionEventListener {
    private static final String HEADER_CONNECTION_TYPE = "X-Connection-Type";
    private static final String HEADER_BUYIN_AMOUNT = "X-BuyIn-Amount";

    private final SessionService sessionService;
    private final TableWebSocketController webSocketController;

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @EventListener
    public void handleEvent(SessionConnectEvent event) {
        log.info("Attempting to connect: {}", event);

        var headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        var connectionTypeHeader = headerAccessor.getNativeHeader(HEADER_CONNECTION_TYPE);
        var buyInAmountHeader = headerAccessor.getNativeHeader(HEADER_BUYIN_AMOUNT);

        if (CollectionUtils.isNotEmpty(connectionTypeHeader)) {
            var connectionType = ConnectionType.valueOf(connectionTypeHeader.getFirst());
            sessionService.putConnectionType(headerAccessor, connectionType);

            if (connectionType == ConnectionType.PLAYER) {
                if (buyInAmountHeader != null && !buyInAmountHeader.isEmpty()) {
                    var buyInAmount = Double.parseDouble(buyInAmountHeader.getFirst());
                    sessionService.putBuyInAmount(headerAccessor, buyInAmount);
                }
            }
        } else {
            sessionService.putConnectionType(headerAccessor, ConnectionType.LISTENER);
        }
    }

    @EventListener
    public void handleEvent(SessionConnectedEvent event) {
        log.info("Connected: {}", event);
    }

    @EventListener
    public void handleEvent(SessionSubscribeEvent event) {
        log.info("New Subscription: {}", event);
    }

    @EventListener
    public void handleEvent(SessionUnsubscribeEvent event) {
        log.info("Un-subscription: {}", event);
    }

    @EventListener
    public void handleEvent(SessionDisconnectEvent event) {
        log.info("Disconnecting: {}", event);
        var principal = event.getUser();
        if (principal == null) {
            log.warn("Session disconnect cannot disconnect player as principal is null");
            return;
        }
        var headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        var tableIdOpt = sessionService.getPokerTableId(headerAccessor);
        if (tableIdOpt.isEmpty()) {
            log.warn("Session disconnect cannot disconnect player as no poker table id found on session");
            return;
        }
        webSocketController.sendDisconnectPlayer(principal, tableIdOpt.get());
    }
}
