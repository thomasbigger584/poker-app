package com.twb.pokergame.web.websocket;

import com.twb.pokergame.web.websocket.dto.PokerAppWebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("WebSocketChatEventListener.handleWebSocketConnectListener");
        logger.info("Received a new web socket connection");
        logger.info("event = " + event);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        logger.info("WebSocketChatEventListener.handleWebSocketDisconnectListener");
        logger.info("event = " + event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            logger.info("Session Attributes is null");
            return;
        }

        if (sessionAttributes.containsKey("username")) {
            String username = (String) sessionAttributes.get("username");

            PokerAppWebSocketMessage chatMessage = new PokerAppWebSocketMessage();
            chatMessage.setType("Leave");
            chatMessage.setSender(username);

            //todo: what is this being sent to ? revise
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
