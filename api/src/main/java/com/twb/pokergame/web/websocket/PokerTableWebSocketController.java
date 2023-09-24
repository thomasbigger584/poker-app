package com.twb.pokergame.web.websocket;

import com.twb.pokergame.domain.enumeration.ConnectionType;
import com.twb.pokergame.service.game.PokerTableGameService;
import com.twb.pokergame.web.websocket.message.client.CreateChatMessageDTO;
import com.twb.pokergame.web.websocket.message.client.CreatePlayerActionDTO;
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
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PokerTableWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(PokerTableWebSocketController.class);

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

        logger.info(">>>> sendPlayerSubscribed - Poker Table: {} - User: {} - Type: {}", tableId, principal.getName(), connectionType);
        ServerMessageDTO message;
        try {
            message = pokerTableGameService.onUserConnected(tableId, connectionType, principal.getName());
            logger.info("<<<< sendPlayerSubscribed - " + message);
        } catch (Exception exception) {
            message = messageFactory.errorMessage(exception.getMessage());
            logger.info("<<<< sendPlayerSubscribed FAILED - " + message);
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
        logger.info(">>>> sendDisconnectPlayer - Poker Table: {} - User: {}", tableId, principal.getName());
        pokerTableGameService.onUserDisconnected(tableId, principal.getName());
    }

    private ConnectionType getConnectionType(StompHeaderAccessor headerAccessor) {
        Optional<ConnectionType> connectionTypeOpt = sessionService.getConnectionType(headerAccessor);
        return connectionTypeOpt.orElse(ConnectionType.LISTENER);
    }
}
