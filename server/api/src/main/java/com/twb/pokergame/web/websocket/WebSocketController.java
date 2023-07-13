package com.twb.pokergame.web.websocket;

import com.twb.pokergame.web.websocket.dto.PokerAppWebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

//    /*
//     * Generic message sent to the subscriber.
//     * Using dot "." in the topic path because rabbitmq doesn't support forward-slash "/" as a separator
//     */
//    @MessageMapping("/ws.sendMessage/{pokerTableId}") // send message endpoint
//    @SendTo("/topic/poker-app-events.{pokerTableId}") // clients subscription endpoint
//    public PokerAppWebSocketMessage sendMessage(@DestinationVariable String pokerTableId,
//                                                @Payload PokerAppWebSocketMessage message) {
//        logger.info("WEBSOCKET (sendMessage) - response to table {} with {}", pokerTableId, message);
//        return message;
//    }
//
//    /*
//     * Specific handlers for message sent to the subscriber
//     */
//
//    @MessageMapping("/ws.newPlayer/{pokerTableId}")
//    @SendTo("/topic/poker-app-events.{pokerTableId}") // clients subscription endpoint
//    public PokerAppWebSocketMessage newUser(@DestinationVariable String pokerTableId,
//                                            @Payload PokerAppWebSocketMessage message,
//                                            SimpMessageHeaderAccessor headerAccessor) {
//        logger.info("WEBSOCKET (newUser) - response to table {} with {}", pokerTableId, message);
//
//        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
//        sessionAttributes.put("username", message.getSender());
//
//        return message;
//    }
}
