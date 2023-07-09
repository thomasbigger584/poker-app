package com.twb.pokergame.web.websocket;

import com.twb.pokergame.web.websocket.dto.WebSocketChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketChatEventListener {
    private final SimpMessageSendingOperations messagingTemplate;


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("WebSocketChatEventListener.handleWebSocketConnectListener");
        System.out.println("event = " + event);
        System.out.println("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("WebSocketChatEventListener.handleWebSocketDisconnectListener");
        System.out.println("event = " + event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            System.out.println("Session Attributes is null");
            return;
        }

        if (sessionAttributes.containsKey("username")) {
            String username = (String) sessionAttributes.get("username");

            WebSocketChatMessage chatMessage = new WebSocketChatMessage();
            chatMessage.setType("Leave");
            chatMessage.setSender(username);
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
