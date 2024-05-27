package com.twb.pokerapp.web.websocket.message.server;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.dto.playeraction.PlayerActionDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.mapper.CardMapper;
import com.twb.pokerapp.mapper.PlayerSessionMapper;
import com.twb.pokerapp.web.websocket.message.server.payload.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerMessageFactory {
    private final PlayerSessionMapper playerSessionMapper;
    private final CardMapper cardMapper;

    public ServerMessageDTO playerSubscribed(List<PlayerSessionDTO> playerSessions) {
        PlayerSubscribedDTO payload = new PlayerSubscribedDTO();
        payload.setPlayerSessions(playerSessions);
        return ServerMessageDTO.create(ServerMessageType.PLAYER_SUBSCRIBED, payload);
    }

    public ServerMessageDTO playerConnected(PlayerSessionDTO playerSession) {
        PlayerConnectedDTO payload = new PlayerConnectedDTO();
        payload.setPlayerSession(playerSession);
        return ServerMessageDTO.create(ServerMessageType.PLAYER_CONNECTED, payload);
    }

    public ServerMessageDTO dealerDetermined(PlayerSession playerSession) {
        DealerDeterminedDTO payload = new DealerDeterminedDTO();
        payload.setPlayerSession(playerSessionMapper.modelToDto(playerSession));
        return ServerMessageDTO.create(ServerMessageType.DEALER_DETERMINED, payload);
    }

    public ServerMessageDTO initDeal(PlayerSession playerSession, Card card) {
        DealPlayerCardDTO payload = new DealPlayerCardDTO();
        payload.setPlayerSession(playerSessionMapper.modelToDto(playerSession));
        payload.setCard(cardMapper.modelToDto(card));
        return ServerMessageDTO.create(ServerMessageType.DEAL_INIT, payload);
    }

    public ServerMessageDTO communityCardDeal(Card card) {
        DealCommunityCardDTO payload = new DealCommunityCardDTO();
        payload.setCard(cardMapper.modelToDto(card));
        return ServerMessageDTO.create(ServerMessageType.DEAL_COMMUNITY, payload);
    }

    public ServerMessageDTO playerTurn(PlayerSession playerSession, ActionType[] actions) {
        PlayerTurnDTO payload = new PlayerTurnDTO();
        payload.setPlayerSession(playerSessionMapper.modelToDto(playerSession));
        payload.setActions(actions);
        return ServerMessageDTO.create(ServerMessageType.PLAYER_TURN, payload);
    }

    public ServerMessageDTO roundFinished() {
        RoundFinishedDTO payload = new RoundFinishedDTO();
        return ServerMessageDTO.create(ServerMessageType.ROUND_FINISHED, payload);
    }

    public ServerMessageDTO gameFinished() {
        GameFinishedDTO payload = new GameFinishedDTO();
        return ServerMessageDTO.create(ServerMessageType.GAME_FINISHED, payload);
    }

    public ServerMessageDTO playerAction(PlayerActionDTO action) {
        PlayerActionEventDTO payload = new PlayerActionEventDTO();
        payload.setAction(action);
        return ServerMessageDTO.create(ServerMessageType.PLAYER_ACTION, payload);
    }

    // TODO: add more ...


    public ServerMessageDTO logMessage(String message) {
        LogMessageDTO payload = new LogMessageDTO();
        payload.setMessage(message);
        return ServerMessageDTO.create(ServerMessageType.LOG, payload);
    }

    public ServerMessageDTO errorMessage(String message) {
        ErrorMessageDTO payload = new ErrorMessageDTO();
        payload.setMessage(message);
        return ServerMessageDTO.create(ServerMessageType.ERROR, payload);
    }

    public ServerMessageDTO playerDisconnected(String username) {
        PlayerDisconnectedDTO payload = new PlayerDisconnectedDTO();
        payload.setUsername(username);
        return ServerMessageDTO.create(ServerMessageType.PLAYER_DISCONNECTED, payload);
    }

    public ServerMessageDTO chatMessage(String username, String message) {
        ChatMessageDTO payload = new ChatMessageDTO();
        payload.setUsername(username);
        payload.setMessage(message);
        return ServerMessageDTO.create(ServerMessageType.CHAT, payload);
    }

}
