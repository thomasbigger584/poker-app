package com.twb.pokergame.web.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokergame.service.game.PokerGameThreadHandler;
import com.twb.pokergame.web.websocket.message.dto.WebSocketMessageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private final PokerGameThreadHandler handler;

    @MessageMapping("/ws.sendMessage") // clients send message to here from outside
    @SendTo("/topic/loops") // clients subscription endpoint
    public WebSocketMessageDTO sendMessage(@Payload WebSocketMessageDTO message) {
        logger.info("WEBSOCKET (sendMessage) with {}", message);

        handler.onPlayerConnected(null, "username");

        return message;
    }














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
