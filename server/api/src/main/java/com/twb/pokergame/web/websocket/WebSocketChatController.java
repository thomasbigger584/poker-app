package com.twb.pokergame.web.websocket;

import com.twb.pokergame.web.websocket.dto.WebSocketChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketChatController {

    @MessageMapping("/chat.sendMessage") // send message endpoint
    @SendTo("/topic/poker-app-events") // clients subscription endpoint
    public WebSocketChatMessage sendMessage(@Payload WebSocketChatMessage message) {
        System.out.println("WebSocketChatController.sendMessage");
        System.out.println("message = " + message);
        return message;
    }




    @MessageMapping("/chat.newUser")
    @SendTo("/topic/javainuse")
    public WebSocketChatMessage newUser(@Payload WebSocketChatMessage message,
                                        SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("WebSocketChatController.newUser");
        System.out.println("message = " + message);

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        sessionAttributes.put("username", message.getSender());

        return message;
    }
}
