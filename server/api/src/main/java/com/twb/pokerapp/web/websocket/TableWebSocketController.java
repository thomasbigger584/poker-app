package com.twb.pokerapp.web.websocket;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.service.game.TableGameService;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.client.CreateChatMessageDTO;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import com.twb.pokerapp.web.websocket.session.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class TableWebSocketController {
    private static final String TOPIC = "/loops.{tableId}";
    private static final String SERVER_MESSAGE_TOPIC = "/topic" + TOPIC;
    private static final String INBOUND_MESSAGE_PREFIX = "/pokerTable/{tableId}";

    private static final String SEND_CHAT_MESSAGE = "/sendChatMessage";
    private static final String SEND_PLAYER_ACTION = "/sendPlayerAction";
    private static final String SEND_DISCONNECT_PLAYER = "/sendDisconnectPlayer";

    private static final String TABLE_ID = "tableId";

    private final SessionService sessionService;
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;
    private final TableGameService tableGameService;

    @SubscribeMapping(TOPIC)
    public ServerMessageDTO sendPlayerSubscribed(Principal principal, StompHeaderAccessor headerAccessor, @DestinationVariable(TABLE_ID) UUID tableId) {
        sessionService.putPokerTableId(headerAccessor, tableId);

        ConnectionType connectionType = getConnectionType(headerAccessor);
        Double buyInAmount = getBuyInAmount(headerAccessor);

        log.info(">>>> sendPlayerSubscribed - Table: {}, User: {}, Connection: {}, BuyIn: {}", tableId, principal.getName(), connectionType, buyInAmount);
        ServerMessageDTO message;
        try {
            message = tableGameService.onUserConnected(tableId, connectionType, principal.getName(), buyInAmount);
            log.info("<<<< sendPlayerSubscribed - " + message);
        } catch (Exception exception) {
            message = messageFactory.errorMessage(exception.getMessage());
            log.info("<<<< sendPlayerSubscribed FAILED - " + message);
        }
        return message;
    }

    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_CHAT_MESSAGE)
    @SendTo(SERVER_MESSAGE_TOPIC)
    public void sendChatMessage(Principal principal, @DestinationVariable(TABLE_ID) UUID tableId, @Payload @Valid CreateChatMessageDTO message) {
        var chatMessage = messageFactory.chatMessage(principal.getName(), message.getMessage());
        dispatcher.send(tableId, chatMessage);
    }

    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_PLAYER_ACTION)
    @SendTo(SERVER_MESSAGE_TOPIC)
    public void sendPlayerAction(Principal principal, @DestinationVariable(TABLE_ID) UUID tableId, @Payload @Valid CreatePlayerActionDTO action) {
        tableGameService.onPlayerAction(tableId, principal.getName(), action);
    }

    // not returning here as called from multiple places
    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_DISCONNECT_PLAYER)
    public void sendDisconnectPlayer(Principal principal, @DestinationVariable(TABLE_ID) UUID tableId) {
        log.info(">>>> sendDisconnectPlayer - Poker Table: {} - User: {}", tableId, principal.getName());
        tableGameService.onUserDisconnected(tableId, principal.getName());
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private ConnectionType getConnectionType(StompHeaderAccessor headerAccessor) {
        return sessionService.getConnectionType(headerAccessor).orElse(ConnectionType.LISTENER);
    }

    private Double getBuyInAmount(StompHeaderAccessor headerAccessor) {
        return sessionService.getBuyInAmount(headerAccessor).orElse(null);
    }
}
