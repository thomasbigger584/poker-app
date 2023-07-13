package com.twb.pokergame.web.websocket;

import com.twb.pokergame.service.game.PokerGameThreadHandler;
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
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    public static final String POKER_GAME_EVENTS_TOPIC = "/topic/loops.{pokerTableId}";

    private final SessionService sessionService;
    private final ServerMessageFactory messageFactory;
    private final PokerGameThreadHandler handler;

    @MessageMapping("/pokerTable/{pokerTableId}/sendConnectPlayer")
    public void sendConnectPlayer(Principal principal,
                                  StompHeaderAccessor headerAccessor,
                                  @DestinationVariable("pokerTableId") String pokerTableId) {
        sessionService.putSessionData(headerAccessor, pokerTableId);
        handler.onPlayerConnected(pokerTableId, principal.getName());
    }

    @MessageMapping("/pokerTable/{pokerTableId}/sendChatMessage")
    @SendTo(POKER_GAME_EVENTS_TOPIC)
    public ServerMessage sendChatMessage(Principal principal,
                                         @DestinationVariable("pokerTableId") String pokerTableId,
                                         @Payload CreateChatMessageDTO message) {
        return messageFactory.chatMessage(principal.getName(), message.getMessage());
    }


    @MessageMapping("/pokerTable/{pokerTableId}/sendDisconnectPlayer")
    public void sendDisconnectPlayer(Principal principal,
                                     @DestinationVariable("pokerTableId") String pokerTableId) {
        handler.onPlayerDisconnected(pokerTableId, principal.getName());
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
