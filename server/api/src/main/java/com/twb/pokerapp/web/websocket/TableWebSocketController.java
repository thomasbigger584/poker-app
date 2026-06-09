package com.twb.pokerapp.web.websocket;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.CreateBotConnectionDTO;
import com.twb.pokerapp.proto.CreateChatMessageDTO;
import com.twb.pokerapp.proto.CreatePlayerActionDTO;
import com.twb.pokerapp.proto.ServerMessageDTO;
import com.twb.pokerapp.service.game.TableGameService;
import com.twb.pokerapp.service.game.thread.dto.PlayerActionCommand;
import com.twb.pokerapp.web.exception.ValidationException;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import com.twb.pokerapp.web.websocket.session.DisconnectGraceService;
import com.twb.pokerapp.web.websocket.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TableWebSocketController {

    private static final int CHAT_MESSAGE_MAX_LENGTH = 50;

    // 1. Logic for Initial Subscription (Spring Intercepted)
    // Client subscribes to: /app/loops.{tableId}
    private static final String SUBSCRIBE_GAME_TOPIC = "loops.{tableId}";

    // 2. Logic for Broadcasting (RabbitMQ Relay)
    // Server sends to: /topic/loops.{tableId}
    private static final String SERVER_MESSAGE_TOPIC = "/topic/loops.{tableId}";

    // 3. Logic for Inbound Actions (Spring Intercepted)
    // Client sends to: /app/pokerTable.{tableId}.sendChatMessage
    private static final String INBOUND_MESSAGE_PREFIX = "pokerTable.{tableId}";

    private static final String SEND_CHAT_MESSAGE = ".sendChatMessage";
    private static final String SEND_PLAYER_ACTION = ".sendPlayerAction";
    private static final String SEND_DISCONNECT_PLAYER = ".sendDisconnectPlayer";
    private static final String SEND_BOT_CONNECTED = ".sendBotConnected";

    private static final String TABLE_ID = "tableId";

    private final SessionService sessionService;
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;
    private final TableGameService tableGameService;
    private final DisconnectGraceService disconnectGraceService;

    /**
     * Triggered when a user subscribes to /app/loops.{tableId}.
     * Returns the initial state of the game directly to the user.
     */
    @SubscribeMapping(SUBSCRIBE_GAME_TOPIC)
    public ServerMessageDTO userSubscribed(Principal principal,
                                           StompHeaderAccessor headerAccessor,
                                           @DestinationVariable(TABLE_ID) UUID tableId) {

        sessionService.putPokerTableId(headerAccessor, tableId);
        // The user is (re)subscribing — cancel any pending grace-period disconnect from a prior drop.
        disconnectGraceService.cancelPending(tableId, principal.getName());
        var connectionType = getConnectionType(headerAccessor);
        var buyInAmount = getBuyInAmount(headerAccessor);
        var reconnect = sessionService.isReconnect(headerAccessor);

        log.debug(">>>> userSubscribed - Table: {}, User: {}, Connection: {}, BuyIn: {}, Reconnect: {}",
                tableId, principal.getName(), connectionType, buyInAmount, reconnect);

        try {
            return tableGameService.onUserConnected(tableId, connectionType, principal.getName(), buyInAmount, reconnect);
        } catch (Exception e) {
            log.error("Failed to subscribe user {} to table {}", principal.getName(), tableId, e);
            return messageFactory.errorMessage(e.getMessage());
        }
    }

    /**
     * Client sends to /app/pokerTable.{tableId}.sendBotConnected
     */
    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_BOT_CONNECTED)
    public void onBotConnected(StompHeaderAccessor headerAccessor,
                               @DestinationVariable(TABLE_ID) UUID tableId,
                               @Payload CreateBotConnectionDTO botConnection) {
        var botUserId = botConnection.getBotUserId();
        if (botUserId.isEmpty()) {
            throw new ValidationException("botUserId", "Bot User ID cannot be null");
        }
        var buyInAmount = ProtoConvert.bigDecimal(botConnection.getBuyInAmount());
        if (buyInAmount == null) {
            throw new ValidationException("buyInAmount", "Buy-In amount cannot be null");
        }
        log.debug(">>>> onBotConnected - Table: {}, BotUser: {}, BuyIn: {}", tableId, botUserId, buyInAmount);

        tableGameService.onBotConnected(tableId, UUID.fromString(botUserId), buyInAmount);
        dispatcher.sendReceipt(headerAccessor);
    }

    /**
     * Client sends to /app/pokerTable.{tableId}.sendChatMessage
     */
    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_CHAT_MESSAGE)
    public void sendChatMessage(Principal principal,
                                StompHeaderAccessor headerAccessor,
                                @DestinationVariable(TABLE_ID) UUID tableId,
                                @Payload CreateChatMessageDTO message) {
        var text = message.getMessage();
        if (text.isBlank() || text.length() > CHAT_MESSAGE_MAX_LENGTH) {
            throw new ValidationException("message", "Chat Message must be between 1 and 50 characters");
        }

        var chatMessage = messageFactory.chatMessage(principal.getName(), text);
        // dispatcher.send should use /topic/loops.{tableId} internally
        dispatcher.send(tableId, chatMessage);
        dispatcher.sendReceipt(headerAccessor);
    }

    /**
     * Client sends to /app/pokerTable.{tableId}.sendPlayerAction
     */
    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_PLAYER_ACTION)
    public void sendPlayerAction(Principal principal,
                                 StompHeaderAccessor headerAccessor,
                                 @DestinationVariable(TABLE_ID) UUID tableId,
                                 @Payload CreatePlayerActionDTO action) {
        var actionType = ProtoConvert.toModel(action.getAction());
        if (actionType == null) {
            throw new ValidationException("action", "Action cannot be null");
        }
        var amount = ProtoConvert.bigDecimal(action.getAmount());
        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("amount", "Amount provided must be positive or zero");
        }

        tableGameService.onPlayerAction(tableId, principal.getName(), new PlayerActionCommand(actionType, amount));
        dispatcher.sendReceipt(headerAccessor);
    }

    /**
     * Client sends to /app/pokerTable.{tableId}.sendDisconnectPlayer
     */
    @MessageMapping(INBOUND_MESSAGE_PREFIX + SEND_DISCONNECT_PLAYER)
    public void sendDisconnectPlayer(Principal principal,
                                     StompHeaderAccessor headerAccessor,
                                     @DestinationVariable(TABLE_ID) UUID tableId) {

        log.debug(">>>> sendDisconnectPlayer - Poker Table: {} - User: {}", tableId, principal.getName());
        tableGameService.onUserDisconnected(tableId, principal.getName());
        dispatcher.sendReceipt(headerAccessor);
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private ConnectionType getConnectionType(StompHeaderAccessor headerAccessor) {
        return sessionService.getConnectionType(headerAccessor).orElse(ConnectionType.LISTENER);
    }

    private BigDecimal getBuyInAmount(StompHeaderAccessor headerAccessor) {
        return sessionService.getBuyInAmount(headerAccessor).orElse(null);
    }
}
