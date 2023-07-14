package com.twb.pokergame.web.websocket;

import com.twb.pokergame.service.game.PokerGameService;
import com.twb.pokergame.web.websocket.message.client.CreateChatMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.web.websocket.message.server.ServerMessageFactory;
import com.twb.pokergame.web.websocket.session.SessionService;
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
public class PokerTableWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(PokerTableWebSocketController.class);
    private static final String POKER_TABLE_EVENTS_TOPIC = "/topic/loops.{pokerTableId}";
    private static final String POKER_TABLE_MESSAGE_PREFIX = "/pokerTable/{pokerTableId}";

    private static final String SEND_CONNECT_PLAYER = "/sendConnectPlayer";
    private static final String SEND_CHAT_MESSAGE = "/sendChatMessage";
    private static final String SEND_DISCONNECT_PLAYER = "/sendDisconnectPlayer";

    private static final String POKER_TABLE_ID = "pokerTableId";

    private final SessionService sessionService;
    private final ServerMessageFactory messageFactory;
    private final PokerGameService gameService;

    /*
     * Note: Always send back to the client a ServerMessage with a specific ServerMessageType and payload.
     * It can either be a return object with @SendTo, or be sent via MessageDispotcher. But not both.
     * Doing both, in my mind, will make messaging to the client more confusing.
     */

    @MessageMapping(POKER_TABLE_MESSAGE_PREFIX + SEND_CONNECT_PLAYER)
    public void sendConnectPlayer(Principal principal, StompHeaderAccessor headerAccessor,
                                  @DestinationVariable(POKER_TABLE_ID) String pokerTableId) {
        sessionService.putPokerTableId(headerAccessor, pokerTableId);
        gameService.onPlayerConnected(pokerTableId, principal.getName());
    }

    @MessageMapping(POKER_TABLE_MESSAGE_PREFIX + SEND_CHAT_MESSAGE)
    @SendTo(POKER_TABLE_EVENTS_TOPIC)
    public ServerMessageDTO sendChatMessage(Principal principal,
                                            @DestinationVariable(POKER_TABLE_ID) String pokerTableId,
                                            @Payload CreateChatMessageDTO message) {
        return messageFactory.chatMessage(principal.getName(), message.getMessage());
    }

    @MessageMapping(POKER_TABLE_MESSAGE_PREFIX + SEND_DISCONNECT_PLAYER)
    public void sendDisconnectPlayer(Principal principal,
                                     @DestinationVariable(POKER_TABLE_ID) String pokerTableId) {
        gameService.onPlayerDisconnected(pokerTableId, principal.getName());
    }
}
