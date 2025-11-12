package com.twb.pokerapp.web.websocket;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.service.game.PokerTableGameService;
import com.twb.pokerapp.web.websocket.message.client.CreateChatMessageDTO;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import com.twb.pokerapp.web.websocket.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class PokerTableWebSocketController {
    private static final String TOPIC = "/loops.{tableId}";
    private static final String SERVER_MESSAGE_TOPIC = "/topic" + TOPIC;
    private static final String INBOUND_MESSAGE_PREFIX = "/pokerTable/{tableId}";

    private static final String SEND_CHAT_MESSAGE = "/sendChatMessage";
    private static final String SEND_PLAYER_ACTION = "/sendPlayerAction";
    private static final String SEND_DISCONNECT_PLAYER = "/sendDisconnectPlayer";

    private static final String POKER_TABLE_ID = "tableId";

    private final SessionService sessionService;
    private final ServerMessageFactory messageFactory;
    private final PokerTableGameService pokerTableGameService;

    @SubscribeMapping(TOPIC)
    public ServerMessageDTO sendPlayerSubscribed(Principal principal, StompHeaderAccessor headerAccessor,
                                                 @DestinationVariable(POKER_TABLE_ID) UUID tableId) {
        ConnectionType connectionType = getConnectionType(headerAccessor);
        sessionService.putPokerTableId(headerAccessor, tableId);

        log.info(">>>> sendPlayerSubscribed - Poker Table: {} - User: {} - Type: {}", tableId, principal.getName(), connectionType);
        ServerMessageDTO message;
        try {
            message = pokerTableGameService.onUserConnected(tableId, connectionType, principal.getName());
            log.info("<<<< sendPlayerSubscribed - " + message);
        } catch (Exception exception) {
            message = messageFactory.errorMessage(exception.getMessage());
            log.info("<<<< sendPlayerSubscribed FAILED - " + message);
        }
        return message;
    }

    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_CHAT_MESSAGE)
    @SendTo(SERVER_MESSAGE_TOPIC)
    public ServerMessageDTO sendChatMessage(Principal principal,
                                            @DestinationVariable(POKER_TABLE_ID) UUID tableId,
                                            @Payload CreateChatMessageDTO message) {
        return messageFactory.chatMessage(principal.getName(), message.getMessage());
    }

    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_PLAYER_ACTION)
    @SendTo(SERVER_MESSAGE_TOPIC)
    public void sendPlayerAction(Principal principal,
                                 @DestinationVariable(POKER_TABLE_ID) UUID tableId,
                                 @Payload CreatePlayerActionDTO action) {
        pokerTableGameService.onPlayerAction(tableId, principal.getName(), action);
    }

    // not returning here as called from multiple places
    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_DISCONNECT_PLAYER)
    public void sendDisconnectPlayer(Principal principal,
                                     @DestinationVariable(POKER_TABLE_ID) UUID tableId) {
        log.info(">>>> sendDisconnectPlayer - Poker Table: {} - User: {}", tableId, principal.getName());
        pokerTableGameService.onUserDisconnected(tableId, principal.getName());
    }

    private ConnectionType getConnectionType(StompHeaderAccessor headerAccessor) {
        return sessionService.getConnectionType(headerAccessor)
                .orElse(ConnectionType.LISTENER);
    }
}
