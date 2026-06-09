package com.twb.pokerapp.web.websocket.message.server;

import com.twb.pokerapp.domain.*;
import com.twb.pokerapp.mapper.*;
import com.twb.pokerapp.proto.*;
import com.twb.pokerapp.service.game.thread.GameSpeedService;
import com.twb.pokerapp.service.game.thread.impl.texas.dto.NextActionsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds the protobuf {@link ServerMessageDTO} envelope. The polymorphic payload is a protobuf
 * {@code oneof}; each factory method sets exactly one payload field, which the client reads back via
 * {@code getPayloadCase()}.
 */
@Component
@RequiredArgsConstructor
public class ServerMessageFactory {
    private final PlayerSessionMapper playerSessionMapper;
    private final PlayerActionMapper playerActionMapper;
    private final RoundMapper roundMapper;
    private final BettingRoundMapper bettingRoundMapper;
    private final RoundPotMapper roundPotMapper;
    private final CardMapper cardMapper;
    private final RoundWinnerMapper roundWinnerMapper;

    private final GameSpeedService gameSpeedService;

    private static ServerMessageDTO.Builder envelope() {
        return ServerMessageDTO.newBuilder().setTimestamp(System.currentTimeMillis());
    }

    public ServerMessageDTO playerSubscribed(List<PlayerSession> playerSessions) {
        return playerSubscribed(playerSessions, null);
    }

    public ServerMessageDTO playerSubscribed(List<PlayerSession> playerSessions, RoundStateDTO roundState) {
        var payload = PlayerSubscribedDTO.newBuilder();
        playerSessions.forEach(session -> payload.addPlayerSessions(playerSessionMapper.modelToDto(session)));
        if (roundState != null) {
            payload.setRoundState(roundState);
        }
        return envelope().setPlayerSubscribed(payload).build();
    }

    public ServerMessageDTO playerConnected(PlayerSessionDTO playerSession) {
        var payload = PlayerConnectedDTO.newBuilder().setPlayerSession(playerSession);
        return envelope().setPlayerConnected(payload).build();
    }

    public ServerMessageDTO dealerDetermined(PlayerSession playerSession) {
        var payload = DealerDeterminedDTO.newBuilder()
                .setPlayerSession(playerSessionMapper.modelToDto(playerSession));
        return envelope().setDealerDetermined(payload).build();
    }

    public ServerMessageDTO initDeal(PlayerSession playerSession, Card card) {
        var payload = DealPlayerCardDTO.newBuilder()
                .setPlayerSession(playerSessionMapper.modelToDto(playerSession))
                .setCard(cardMapper.modelToDto(card));
        return envelope().setDealInit(payload).build();
    }

    public ServerMessageDTO communityCardDeal(Card card) {
        var payload = DealCommunityCardDTO.newBuilder().setCard(cardMapper.modelToDto(card));
        return envelope().setDealCommunity(payload).build();
    }

    public ServerMessageDTO playerTurn(PlayerSession playerSession,
                                       BettingRound bettingRound,
                                       NextActionsDTO nextActionsDto,
                                       long playerTurnWaitMs) {
        var payload = PlayerTurnDTO.newBuilder()
                .setPlayerSession(playerSessionMapper.modelToDto(playerSession))
                .setBettingRound(bettingRoundMapper.modelToDto(bettingRound))
                .setAmountToCall(ProtoConvert.money(nextActionsDto.amountToCall()))
                .setPlayerTurnWaitMs(gameSpeedService.getPlayerTurnWait(bettingRound, playerTurnWaitMs));
        for (var action : nextActionsDto.nextActions()) {
            payload.addNextActions(ProtoConvert.toProto(action));
        }
        return envelope().setPlayerTurn(payload).build();
    }

    public ServerMessageDTO playerActioned(PlayerAction playerAction) {
        var payload = PlayerActionedDTO.newBuilder().setAction(playerActionMapper.modelToDto(playerAction));
        return envelope().setPlayerActioned(payload).build();
    }

    public ServerMessageDTO bettingRoundUpdated(Round round, BettingRound bettingRound, List<RoundPot> roundPots) {
        var payload = BettingRoundUpdatedDTO.newBuilder()
                .setRound(roundMapper.modelToDto(round))
                .setBettingRound(bettingRoundMapper.modelToDto(bettingRound));
        roundPots.forEach(roundPot -> payload.addRoundPots(roundPotMapper.modelToDto(roundPot)));
        return envelope().setBettingRoundUpdated(payload).build();
    }

    public ServerMessageDTO roundFinished(List<RoundWinner> winners) {
        var payload = RoundFinishedDTO.newBuilder();
        winners.forEach(winner -> payload.addWinners(roundWinnerMapper.modelToDto(winner)));
        return envelope().setRoundFinished(payload).build();
    }

    public ServerMessageDTO gameFinished() {
        return envelope().setGameFinished(GameFinishedDTO.getDefaultInstance()).build();
    }

    public ServerMessageDTO logMessage(String message) {
        var payload = LogMessageDTO.newBuilder().setMessage(ProtoConvert.text(message));
        return envelope().setLog(payload).build();
    }

    public ServerMessageDTO errorMessage(String message) {
        var payload = ErrorMessageDTO.newBuilder().setMessage(ProtoConvert.text(message));
        return envelope().setError(payload).build();
    }

    public ServerMessageDTO playerDisconnected(String username) {
        var payload = PlayerDisconnectedDTO.newBuilder().setUsername(ProtoConvert.text(username));
        return envelope().setPlayerDisconnected(payload).build();
    }

    public ServerMessageDTO chatMessage(String username, String message) {
        var payload = ChatMessageDTO.newBuilder()
                .setUsername(ProtoConvert.text(username))
                .setMessage(ProtoConvert.text(message));
        return envelope().setChat(payload).build();
    }

    public ServerMessageDTO validationErrors(ValidationDTO validationDto) {
        return envelope().setValidation(validationDto).build();
    }
}
