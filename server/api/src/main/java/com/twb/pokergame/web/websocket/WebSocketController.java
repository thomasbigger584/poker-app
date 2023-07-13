package com.twb.pokergame.web.websocket;

import com.twb.pokergame.service.game.PokerGameThreadHandler;
import com.twb.pokergame.web.websocket.message.MessageDispatcher;
import com.twb.pokergame.web.websocket.message.client.CreateChatMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessage;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
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
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher messageDispatcher;

    private final PokerGameThreadHandler handler;

    @MessageMapping("/pokerTable/{pokerTableId}/sendChatMessage")
    @SendTo("/topic/loops.{pokerTableId}")
    public ServerMessage sendChatMessage(@DestinationVariable("pokerTableId") String pokerTableId,
                                         @Payload CreateChatMessageDTO message) {
        logger.info("sendChatMessage - PokerTable: {}, User: {}, Message: {}" , pokerTableId, "placeholder", message.getMessage());
        return messageFactory.chatMessage("placeholder", message.getMessage());
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
