package com.twb.pokerapp.web.websocket.message.server;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.mapper.BettingRoundMapper;
import com.twb.pokerapp.mapper.CardMapper;
import com.twb.pokerapp.mapper.PlayerActionMapper;
import com.twb.pokerapp.mapper.PlayerSessionMapper;
import com.twb.pokerapp.web.websocket.message.server.payload.*;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationDTO;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerMessageFactory {
    private final PlayerSessionMapper playerSessionMapper;
    private final PlayerActionMapper playerActionMapper;
    private final BettingRoundMapper bettingRoundMapper;
    private final CardMapper cardMapper;

    public ServerMessageDTO playerSubscribed(List<PlayerSessionDTO> playerSessions) {
        var payload = new PlayerSubscribedDTO();
        payload.setPlayerSessions(playerSessions);
        return ServerMessageDTO.create(ServerMessageType.PLAYER_SUBSCRIBED, payload);
    }

    public ServerMessageDTO playerConnected(PlayerSessionDTO playerSession) {
        var payload = new PlayerConnectedDTO();
        payload.setPlayerSession(playerSession);
        return ServerMessageDTO.create(ServerMessageType.PLAYER_CONNECTED, payload);
    }

    public ServerMessageDTO dealerDetermined(PlayerSession playerSession) {
        var payload = new DealerDeterminedDTO();
        payload.setPlayerSession(playerSessionMapper.modelToDto(playerSession));
        return ServerMessageDTO.create(ServerMessageType.DEALER_DETERMINED, payload);
    }

    public ServerMessageDTO initDeal(PlayerSession playerSession, Card card) {
        var payload = new DealPlayerCardDTO();
        payload.setPlayerSession(playerSessionMapper.modelToDto(playerSession));
        payload.setCard(cardMapper.modelToDto(card));
        return ServerMessageDTO.create(ServerMessageType.DEAL_INIT, payload);
    }

    public ServerMessageDTO communityCardDeal(Card card) {
        var payload = new DealCommunityCardDTO();
        payload.setCard(cardMapper.modelToDto(card));
        return ServerMessageDTO.create(ServerMessageType.DEAL_COMMUNITY, payload);
    }

    public ServerMessageDTO playerTurn(PlayerSession playerSession,
                                       @Nullable PlayerAction prevPlayerAction,
                                       BettingRound bettingRound,
                                       ActionType[] nextActions,
                                       Double amountToCall) {
        var payload = new PlayerTurnDTO();
        payload.setPlayerSession(playerSessionMapper.modelToDto(playerSession));
        if (prevPlayerAction != null) {
            payload.setPrevPlayerAction(playerActionMapper.modelToDto(prevPlayerAction));
        }
        payload.setBettingRound(bettingRoundMapper.modelToDto(bettingRound));
        payload.setNextActions(nextActions);
        payload.setAmountToCall(amountToCall);
        return ServerMessageDTO.create(ServerMessageType.PLAYER_TURN, payload);
    }

    public ServerMessageDTO roundFinished() {
        var payload = new RoundFinishedDTO();
        return ServerMessageDTO.create(ServerMessageType.ROUND_FINISHED, payload);
    }

    public ServerMessageDTO gameFinished() {
        var payload = new GameFinishedDTO();
        return ServerMessageDTO.create(ServerMessageType.GAME_FINISHED, payload);
    }

    // TODO: add more ...


    public ServerMessageDTO logMessage(String message) {
        var payload = new LogMessageDTO();
        payload.setMessage(message);
        return ServerMessageDTO.create(ServerMessageType.LOG, payload);
    }

    public ServerMessageDTO errorMessage(String message) {
        var payload = new ErrorMessageDTO();
        payload.setMessage(message);
        return ServerMessageDTO.create(ServerMessageType.ERROR, payload);
    }

    public ServerMessageDTO playerDisconnected(String username) {
        var payload = new PlayerDisconnectedDTO();
        payload.setUsername(username);
        return ServerMessageDTO.create(ServerMessageType.PLAYER_DISCONNECTED, payload);
    }

    public ServerMessageDTO chatMessage(String username, String message) {
        var payload = new ChatMessageDTO();
        payload.setUsername(username);
        payload.setMessage(message);
        return ServerMessageDTO.create(ServerMessageType.CHAT, payload);
    }

    public ServerMessageDTO validationErrors(ValidationDTO validationDto) {
        return ServerMessageDTO.create(ServerMessageType.VALIDATION, validationDto);
    }
}
