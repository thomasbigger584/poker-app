package com.twb.pokergame.web.websocket;

import com.twb.pokergame.service.game.GameConnectionService;
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
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PokerTableWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(PokerTableWebSocketController.class);

    private static final String EVENTS_TOPIC_SUFFIX = "/loops.{tableId}";
    private static final String EVENTS_TOPIC = "/topic" + EVENTS_TOPIC_SUFFIX;
    private static final String POKER_TABLE_MESSAGE_PREFIX = "/pokerTable/{tableId}";

    private static final String SEND_CHAT_MESSAGE = "/sendChatMessage";
    private static final String SEND_DISCONNECT_PLAYER = "/sendDisconnectPlayer";

    private static final String POKER_TABLE_ID = "tableId";

    private final SessionService sessionService;
    private final ServerMessageFactory messageFactory;
    private final GameConnectionService gameConnectionService;

    @SubscribeMapping(EVENTS_TOPIC_SUFFIX)
    public ServerMessageDTO sendPlayerSubscribed(Principal principal, StompHeaderAccessor headerAccessor,
                                                 @DestinationVariable(POKER_TABLE_ID) UUID tableId) {
        logger.info(">>>> sendPlayerSubscribed - Poker Table: {} - User: {}", tableId, principal.getName());
        sessionService.putPokerTableId(headerAccessor, tableId);
        ServerMessageDTO message = gameConnectionService.onPlayerSubscribed(tableId, principal.getName());
        logger.info("<<<< sendPlayerSubscribed - " + message);
        return message;
    }

    @MessageMapping(POKER_TABLE_MESSAGE_PREFIX + SEND_CHAT_MESSAGE)
    @SendTo(EVENTS_TOPIC)
    public ServerMessageDTO sendChatMessage(Principal principal,
                                            @DestinationVariable(POKER_TABLE_ID) UUID tableId,
                                            @Payload CreateChatMessageDTO message) {
        return messageFactory.chatMessage(principal.getName(), message.getMessage());
    }

    // not returning here as called from multiple places
    @MessageMapping(POKER_TABLE_MESSAGE_PREFIX + SEND_DISCONNECT_PLAYER)
    public void sendDisconnectPlayer(Principal principal,
                                     @DestinationVariable(POKER_TABLE_ID) UUID tableId) {
        logger.info(">>>> sendDisconnectPlayer - Poker Table: {} - User: {}", tableId, principal.getName());
        gameConnectionService.onPlayerDisconnected(tableId, principal.getName());
    }
}
