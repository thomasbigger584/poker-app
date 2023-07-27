package com.twb.pokergame.web.websocket.message.server;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.domain.enumeration.CardType;
import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.mapper.CardMapper;
import com.twb.pokergame.mapper.PlayerSessionMapper;
import com.twb.pokergame.web.websocket.message.server.payload.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServerMessageFactory {
    private final PlayerSessionMapper playerSessionMapper;
    private final CardMapper cardMapper;

    public ServerMessageDTO playerSubscribed(List<PlayerSessionDTO> playerSessions) {
        PlayerSubscribedDTO payload = PlayerSubscribedDTO.builder()
                .playerSessions(playerSessions)
                .build();
        return ServerMessageDTO.create(ServerMessageType.PLAYER_SUBSCRIBED, payload);
    }

    public ServerMessageDTO playerConnected(PlayerSessionDTO playerSession) {
        PlayerConnectedDTO payload = PlayerConnectedDTO.builder()
                .playerSession(playerSession)
                .build();
        return ServerMessageDTO.create(ServerMessageType.PLAYER_CONNECTED, payload);
    }

    public ServerMessageDTO dealerDetermined(PlayerSession playerSession) {
        DealerDeterminedDTO payload = DealerDeterminedDTO.builder()
                .playerSession(playerSessionMapper.modelToDto(playerSession))
                .build();
        return ServerMessageDTO.create(ServerMessageType.DEALER_DETERMINED, payload);
    }

    public ServerMessageDTO initDeal(PlayerSession playerSession, Card card) {
        DealPlayerCardDTO payload = DealPlayerCardDTO.builder()
                .playerSession(playerSessionMapper.modelToDto(playerSession))
                .card(cardMapper.modelToDto(card))
                .build();
        return ServerMessageDTO.create(ServerMessageType.DEAL_INIT, payload);
    }

    public ServerMessageDTO communityCardDeal(Card card) {
        DealCommunityCardDTO payload = DealCommunityCardDTO.builder()
                .card(cardMapper.modelToDto(card))
                .build();
        return ServerMessageDTO.create(ServerMessageType.DEAL_COMMUNITY, payload);
    }


    // TODO: add more ...


    public ServerMessageDTO logMessage(String message) {
        LogMessageDTO payload = LogMessageDTO.builder()
                .message(message)
                .build();
        return ServerMessageDTO.create(ServerMessageType.LOG, payload);
    }

    public ServerMessageDTO playerDisconnected(String username) {
        PlayerDisconnectedDTO payload = PlayerDisconnectedDTO.builder()
                .username(username)
                .build();
        return ServerMessageDTO.create(ServerMessageType.PLAYER_DISCONNECTED, payload);
    }

    public ServerMessageDTO chatMessage(String username, String message) {
        ChatMessageDTO payload = ChatMessageDTO.builder()
                .username(username)
                .message(message)
                .build();
        return ServerMessageDTO.create(ServerMessageType.CHAT, payload);
    }


}
