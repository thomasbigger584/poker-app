package com.twb.pokergame.web.websocket;

import com.twb.pokergame.service.game.PokerGameThreadHandler;
import com.twb.pokergame.web.websocket.message.client.GenericTestMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private final PokerGameThreadHandler handler;

    @MessageMapping("/pokerTable/{pokerTableId}/sendMessage") // client hits this endpoint with message
    @SendTo("/topic/loops.{pokerTableId}") // subscribe and send messages to topic
    public GenericTestMessageDTO sendMessage(@DestinationVariable("pokerTableId") String pokerTableId,
                                     @Payload GenericTestMessageDTO message) {
        logger.info("WEBSOCKET (sendMessage) with {}", message);

//        handler.onPlayerConnected(pokerTableId, "username");

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
