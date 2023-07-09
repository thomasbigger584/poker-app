package com.twb.pokergame.web.websocket;

import com.twb.pokergame.web.websocket.dto.PokerAppWebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class PokerAppWebSocketController {

    /*
     * Generic message sent to the subscriber
     */
    @MessageMapping("/ws.sendMessage") // send message endpoint
    @SendTo("/topic/poker-app-events") // clients subscription endpoint
    public PokerAppWebSocketMessage sendMessage(@Payload PokerAppWebSocketMessage message) {
        System.out.println("WebSocketChatController.sendMessage");
        System.out.println("message = " + message);
        return message;
    }


    /*
     * Specific handlers for message sent to the subscriber
     */

    @MessageMapping("/ws.newUser")
    @SendTo("/topic/poker-app-events") // clients subscription endpoint
    public PokerAppWebSocketMessage newUser(@Payload PokerAppWebSocketMessage message,
                                            SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("WebSocketChatController.newUser");
        System.out.println("message = " + message);

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        sessionAttributes.put("username", message.getSender());

        return message;
    }
}
