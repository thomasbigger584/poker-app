package com.twb.pokerapp.testutils.validator;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.CardType;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.dto.appuser.AppUserDTO;
import com.twb.pokerapp.dto.card.CardDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.dto.table.TableDTO;
import com.twb.pokerapp.testutils.game.GameRunnerParams;
import com.twb.pokerapp.testutils.http.message.PlayersServerMessages;
import com.twb.pokerapp.testutils.sql.SqlClient;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerSubscribedDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.twb.pokerapp.testutils.fixture.HandFixture.findCard;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public abstract class Validator {
    protected final GameRunnerParams params;
    protected final SqlClient sqlClient;

    public void validateHandleMessage(ServerMessageDTO message) {

        onValidateHandleMessage(message);
    }

    public void validateEndOfRun(PlayersServerMessages messages) {
        var listenerMessages = messages.getListenerMessages();
        assertPlayersConnected(listenerMessages);
        assertPlayersSubscribed(listenerMessages);

        onValidateEndOfRun(messages);

        assertPlayersDisconnect();
    }

    private void assertPlayersConnected(List<ServerMessageDTO> listenerMessages) {
        var messages = get(listenerMessages, ServerMessageType.PLAYER_CONNECTED);
        assertEquals(2, messages.size());
        messages.forEach(message -> {
            var payload = (PlayerConnectedDTO) message.getPayload();

            var playerSessionDto = payload.getPlayerSession();
            assertPlayerSession(playerSessionDto);

            var tableDto = playerSessionDto.getPokerTable();
            assertTable(tableDto);
        });
    }

    private void assertPlayersSubscribed(List<ServerMessageDTO> listenerMessages) {
        var messages = get(listenerMessages, ServerMessageType.PLAYER_SUBSCRIBED);
        assertEquals(1, messages.size());
        messages.forEach(message -> {
            var payload = (PlayerSubscribedDTO) message.getPayload();

            var playerSessions = payload.getPlayerSessions();
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
        assertEquals(3, playerSessions.size());

        var listenerPlayerSessionList = playerSessions.stream()
                .filter(playerSession -> playerSession.getUser().getUsername().equals("viewer1")).toList();
        assertEquals(1, listenerPlayerSessionList.size());
        assertPlayerDisconnect(listenerPlayerSessionList.getFirst());

        var playerPlayerSessionList = playerSessions.stream()
                .filter(playerSession -> playerSession.getUser().getUsername().startsWith("user")).toList();
        assertEquals(2, playerPlayerSessionList.size());
        playerPlayerSessionList.forEach(this::assertPlayerDisconnect);
    }

    private void assertPlayerDisconnect(PlayerSession playerSession) {
        assertNull(playerSession.getDealer());
        assertNull(playerSession.getFunds());
        assertNull(playerSession.getPokerTable());
        assertNull(playerSession.getConnectionType());
        assertEquals(SessionState.DISCONNECTED, playerSession.getSessionState());
    }

    // ***************************************************************
    // Common Entity Assertions
    // ***************************************************************

    protected void assertCard(CardDTO cardDto, CardType cardType) {
        var foundCardFromDeck = findCard(cardDto.getRankType(), cardDto.getSuitType());
        assertEquals(cardDto.getRankType(), foundCardFromDeck.getRankType());
        assertEquals(cardDto.getRankValue(), foundCardFromDeck.getRankValue());
        assertEquals(cardDto.getSuitType(), foundCardFromDeck.getSuitType());
        assertEquals(cardDto.getCardType(), cardType);

        var cardOpt = sqlClient.getCard(cardDto.getId());
        assertTrue(cardOpt.isPresent());
        var card = cardOpt.get();

        assertEquals(cardDto.getId(), card.getId());
        assertEquals(cardDto.getRankType(), card.getRankType());
        assertEquals(cardDto.getRankValue(), card.getRankValue());
        assertEquals(cardDto.getSuitType(), card.getSuitType());
        assertEquals(cardDto.getCardType(), card.getCardType());
    }

    protected void assertPlayerSession(PlayerSessionDTO playerSessionDto) {
        assertEquals(SessionState.CONNECTED, playerSessionDto.getSessionState());

        var playerSessionId = playerSessionDto.getId();
        var playerSessionOpt = sqlClient.getPlayerSession(playerSessionId);
        assertTrue(playerSessionOpt.isPresent());
        var playerSession = playerSessionOpt.get();
        assertEquals(playerSessionId, playerSession.getId());
        if (playerSessionDto.getConnectionType() == ConnectionType.PLAYER) {
            assertTrue(playerSessionDto.getPosition() > 0);
        } else {
            assertNull(playerSessionDto.getPosition());
        }
        assertEquals(playerSessionDto.getPosition(), playerSession.getPosition());

        assertAppUser(playerSessionDto.getUser());
        assertTable(playerSessionDto.getPokerTable());
    }

    protected void assertAppUser(AppUserDTO appUserDto) {
        var appUserId = appUserDto.getId();
        var appUserOpt = sqlClient.getAppUser(appUserId);
        assertTrue(appUserOpt.isPresent());
        var appUser = appUserOpt.get();
        assertEquals(appUserId, appUser.getId());
        assertEquals(appUserDto.getUsername(), appUser.getUsername());
        assertEquals(appUserDto.getFirstName(), appUser.getFirstName());
        assertEquals(appUserDto.getLastName(), appUser.getLastName());
        assertEquals(appUserDto.getEmail(), appUser.getEmail());
        assertEquals(appUserDto.isEmailVerified(), appUser.isEmailVerified());
        assertEquals(appUserDto.isEnabled(), appUser.isEnabled());
        assertTrue(appUser.isEnabled());
    }

    protected void assertTable(TableDTO tableDto) {
        var tableId = tableDto.getId();
        var tableOpt = sqlClient.getPokerTable(tableId);
        assertTrue(tableOpt.isPresent());
        var table = tableOpt.get();
        assertEquals(tableId, table.getId());
        assertEquals(tableDto.getName(), table.getName());
        assertEquals(tableDto.getGameType(), table.getGameType());
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    public List<ServerMessageDTO> get(int userIndex, PlayersServerMessages messages, ServerMessageType type) {
        var userMessages = messages.get("user" + userIndex);
        return get(userMessages, type);
    }

    protected List<ServerMessageDTO> get(List<ServerMessageDTO> messages, ServerMessageType type) {
        return messages.stream()
                .filter(message -> message.getType() == type)
                .toList();
    }

    // ***************************************************************
    // Abstract Methods
    // ***************************************************************

    protected abstract void onValidateHandleMessage(ServerMessageDTO message);

    protected abstract void onValidateEndOfRun(PlayersServerMessages messages);
}
