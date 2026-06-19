package com.twb.pokerapp.testutils.validator;

import com.twb.pokerapp.domain.*;
import com.twb.pokerapp.proto.CardType;
import com.twb.pokerapp.proto.ConnectionType;
import com.twb.pokerapp.proto.SessionState;
import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.AppUserDTO;
import com.twb.pokerapp.proto.BettingRoundDTO;
import com.twb.pokerapp.proto.CardDTO;
import com.twb.pokerapp.proto.PlayerActionDTO;
import com.twb.pokerapp.proto.PlayerSessionDTO;
import com.twb.pokerapp.proto.RoundDTO;
import com.twb.pokerapp.proto.ServerMessageDTO;
import com.twb.pokerapp.proto.TableDTO;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioParams;
import com.twb.pokerapp.testutils.http.message.PlayersServerMessages;
import com.twb.pokerapp.testutils.sql.SqlClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.twb.pokerapp.testutils.fixture.HandFixture.findCard;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public abstract class Validator {
    protected final ScenarioParams params;
    protected final SqlClient sqlClient;

    public void validateHandleMessage(ServerMessageDTO message) {
        onValidateHandleMessage(message);
    }

    public void validateEndOfRun(PlayersServerMessages messages) {
        validateEndOfRunConnections(messages);
        onValidateEndOfRun(messages);
    }

    public void validateEndOfRunConnections(PlayersServerMessages messages) {
        var listenerMessages = messages.getListenerMessages();
        assertPlayersConnected(listenerMessages);
        assertPlayersSubscribed(listenerMessages);
        assertPlayersDisconnect();
    }

    public void validateInvalidAction(PlayersServerMessages messages) {
        var validationMessages = get(2, messages, ServerMessageDTO.PayloadCase.VALIDATION);
        assertEquals(1, validationMessages.size());
        var validationMessage = validationMessages.getFirst();
        var validationDto = validationMessage.getValidation();
        var fields = validationDto.getFieldsList();

        // The server validates fail-fast: a missing/unspecified action short-circuits before the
        // amount check, so exactly one "action" validation field is reported.
        assertEquals(1, fields.size());
        assertEquals("action", fields.getFirst().getField());
    }

    // ***************************************************************
    // Lifecycle Assertions
    // ***************************************************************

    private void assertPlayersConnected(List<ServerMessageDTO> listenerMessages) {
        var messages = get(listenerMessages, ServerMessageDTO.PayloadCase.PLAYER_CONNECTED);
        assertEquals(params.getScenarioPlayers().size(), messages.size());
        messages.forEach(message -> {
            var payload = message.getPlayerConnected();

            var playerSessionDto = payload.getPlayerSession();
            assertPlayerSession(playerSessionDto);

            var tableDto = playerSessionDto.getPokerTable();
            assertTable(tableDto);
        });
    }

    private void assertPlayersSubscribed(List<ServerMessageDTO> listenerMessages) {
        var messages = get(listenerMessages, ServerMessageDTO.PayloadCase.PLAYER_SUBSCRIBED);
        assertEquals(1, messages.size());
        messages.forEach(message -> {
            var payload = message.getPlayerSubscribed();

            var playerSessions = payload.getPlayerSessionsList();
            assertFalse(playerSessions.isEmpty());

            var viewerSessionOpt = playerSessions.stream()
                    .filter(playerSessionDto ->
                            playerSessionDto.getUser().getUsername().equals("viewer1")).findFirst();
            assertTrue(viewerSessionOpt.isPresent());

            for (var playerSessionDto : playerSessions) {
                assertPlayerSession(playerSessionDto);
            }
        });
    }

    private void assertPlayersDisconnect() {
        var playerSessions = sqlClient.getPlayerSessions();
        var scenarioPlayersSize = params.getScenarioPlayers().size();
        assertEquals(scenarioPlayersSize + 1, playerSessions.size());

        var listenerPlayerSessionList = playerSessions.stream()
                .filter(playerSession -> playerSession.getUser().getUsername().equals("viewer1")).toList();
        assertEquals(1, listenerPlayerSessionList.size());
        assertPlayerDisconnect(listenerPlayerSessionList.getFirst());

        var playerPlayerSessionList = playerSessions.stream()
                .filter(playerSession -> playerSession.getUser().getUsername().startsWith("user")).toList();
        assertEquals(scenarioPlayersSize, playerPlayerSessionList.size());
        playerPlayerSessionList.forEach(this::assertPlayerDisconnect);
    }

    private void assertPlayerDisconnect(PlayerSession playerSession) {
        var username = playerSession.getUser().getUsername();
        assertNull(playerSession.getDealer(), "Dealer should be null for user " + username);
        assertNull(playerSession.getFunds(), "Funds should be null for user " + username);
        assertNull(playerSession.getPokerTable(), "Table should be null for user " + username);
        assertNull(playerSession.getConnectionType(), "ConnectionType should be null for user " + username);
        assertEquals(SessionState.SESSION_STATE_DISCONNECTED, playerSession.getSessionState(), "SessionState should be DISCONNECTED for user " + username);
    }

    // ***************************************************************
    // Common Entity Assertions
    // ***************************************************************

    protected PlayerAction assertPlayerAction(PlayerActionDTO playerActionDto) {
        var playerActionId = playerActionDto.getId();
        var playerActionOpt = sqlClient.getPlayerAction(UUID.fromString(playerActionId));
        assertTrue(playerActionOpt.isPresent());
        var playerAction = playerActionOpt.get();
        assertEquals(playerActionId, playerAction.getId().toString());
        assertEquals(playerActionDto.getActionType(), playerAction.getActionType());
        var dtoAmount = ProtoConvert.bigDecimal(playerActionDto.getAmount());
        if (dtoAmount == null) {
            assertNull(playerAction.getAmount());
        } else {
            assertEquals(0, dtoAmount.compareTo(playerAction.getAmount()));
        }

        assertPlayerSession(playerActionDto.getPlayerSession());
        assertBettingRound(playerActionDto.getBettingRound());
        return playerAction;
    }

    protected BettingRound assertBettingRound(BettingRoundDTO bettingRoundDto) {
        var bettingRoundId = bettingRoundDto.getId();
        var bettingRoundOpt = sqlClient.getBettingRound(UUID.fromString(bettingRoundId));
        assertTrue(bettingRoundOpt.isPresent());
        var bettingRound = bettingRoundOpt.get();
        assertEquals(bettingRoundId, bettingRound.getId().toString());
        assertEquals(bettingRoundDto.getType(), bettingRound.getType());
        return bettingRound;
    }

    protected Card assertCard(CardDTO cardDto, CardType cardType) {
        var rankType = cardDto.getRankType();
        var suitType = cardDto.getSuitType();
        var foundCardFromDeck = findCard(rankType, suitType);
        assertEquals(rankType, foundCardFromDeck.getRankType());
        assertEquals(cardDto.getRankValue(), foundCardFromDeck.getRankValue());
        assertEquals(suitType, foundCardFromDeck.getSuitType());
        assertEquals(cardDto.getCardType(), cardType);

        var cardOpt = sqlClient.getCard(UUID.fromString(cardDto.getId()));
        assertTrue(cardOpt.isPresent());
        var card = cardOpt.get();

        assertEquals(cardDto.getId(), card.getId().toString());
        assertEquals(rankType, card.getRankType());
        assertEquals(cardDto.getRankValue(), card.getRankValue());
        assertEquals(suitType, card.getSuitType());
        assertEquals(cardDto.getCardType(), card.getCardType());
        return card;
    }

    protected Round assertRound(RoundDTO roundDto) {
        var roundId = roundDto.getId();
        var roundOpt = sqlClient.getRound(UUID.fromString(roundId));
        assertTrue(roundOpt.isPresent());
        var round = roundOpt.get();
        assertEquals(roundId, round.getId().toString());
        return round;
    }

    protected PlayerSession assertPlayerSession(PlayerSessionDTO playerSessionDto) {
        assertEquals(SessionState.SESSION_STATE_CONNECTED, playerSessionDto.getSessionState());

        var playerSessionId = playerSessionDto.getId();
        var playerSessionOpt = sqlClient.getPlayerSession(UUID.fromString(playerSessionId));
        assertTrue(playerSessionOpt.isPresent());
        var playerSession = playerSessionOpt.get();
        assertEquals(playerSessionId, playerSession.getId().toString());
        if (playerSessionDto.getConnectionType() == ConnectionType.CONNECTION_TYPE_PLAYER) {
            assertTrue(playerSessionDto.getPosition() > 0);
            assertEquals(Integer.valueOf(playerSessionDto.getPosition()), playerSession.getPosition());
        } else {
            assertFalse(playerSessionDto.hasPosition());
            assertNull(playerSession.getPosition());
        }
        assertAppUser(playerSessionDto.getUser());
        assertTable(playerSessionDto.getPokerTable());
        return playerSession;
    }

    protected AppUser assertAppUser(AppUserDTO appUserDto) {
        var appUserId = appUserDto.getId();
        var appUserOpt = sqlClient.getAppUser(UUID.fromString(appUserId));
        assertTrue(appUserOpt.isPresent());
        var appUser = appUserOpt.get();
        assertEquals(appUserId, appUser.getId().toString());
        assertEquals(appUserDto.getUsername(), appUser.getUsername());
        // proto3 plain strings cannot be null: an absent name/email round-trips as "", so normalise
        // the nullable entity values before comparing against the proto canonical empty string.
        assertEquals(appUserDto.getFirstName(), StringUtils.defaultString(appUser.getFirstName()));
        assertEquals(appUserDto.getLastName(), StringUtils.defaultString(appUser.getLastName()));
        assertEquals(appUserDto.getEnabled(), appUser.isEnabled());
        assertTrue(appUser.isEnabled());
        if (appUser instanceof PhysicalUser physicalUser) {
            assertEquals(appUserDto.getEmail(), StringUtils.defaultString(physicalUser.getEmail()));
            assertEquals(appUserDto.getEmailVerified(), physicalUser.isEmailVerified());
        }
        return appUser;
    }

    protected PokerTable assertTable(TableDTO tableDto) {
        var tableId = tableDto.getId();
        var tableOpt = sqlClient.getPokerTable(UUID.fromString(tableId));
        assertTrue(tableOpt.isPresent());
        var table = tableOpt.get();
        assertEquals(tableId, table.getId().toString());
        assertEquals(tableDto.getName(), table.getName());
        assertEquals(tableDto.getGameType(), table.getGameType());
        return table;
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    public List<ServerMessageDTO> get(int userIndex, PlayersServerMessages messages, ServerMessageDTO.PayloadCase payloadCase) {
        var userMessages = messages.get("user" + userIndex);
        return get(userMessages, payloadCase);
    }

    protected List<ServerMessageDTO> get(List<ServerMessageDTO> messages, ServerMessageDTO.PayloadCase payloadCase) {
        return messages.stream()
                .filter(message -> message.getPayloadCase() == payloadCase)
                .sorted(Comparator.comparingLong(ServerMessageDTO::getTimestamp))
                .toList();
    }

    // ***************************************************************
    // Abstract Methods
    // ***************************************************************

    protected abstract void onValidateHandleMessage(ServerMessageDTO message);

    protected abstract void onValidateEndOfRun(PlayersServerMessages messages);
}
