package com.twb.pokerapp.utils.validator;

import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.utils.game.GameRunnerParams;
import com.twb.pokerapp.utils.http.message.PlayersServerMessages;
import com.twb.pokerapp.utils.sql.SqlClient;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerSubscribedDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    }

    private void assertPlayersConnected(List<ServerMessageDTO> listenerMessages) {
        var playerConnectedMessages = get(listenerMessages, ServerMessageType.PLAYER_CONNECTED);
        assertEquals(2, playerConnectedMessages.size());

        playerConnectedMessages.forEach(message -> {
            var payload = (PlayerConnectedDTO) message.getPayload();

            // PlayerSession Assertions
            var playerSessionDto = payload.getPlayerSession();
            assertEquals(SessionState.CONNECTED, playerSessionDto.getSessionState(), "PlayerSession state should be CONNECTED");

            var playerSessionId = playerSessionDto.getId();
            var playerSessionOpt = sqlClient.getPlayerSession(playerSessionId);
            assertTrue(playerSessionOpt.isPresent(), "PlayerSession not found for ID");
            var playerSession = playerSessionOpt.get();
            assertEquals(playerSessionId, playerSession.getId(), "PlayerSession IDs do not match");
            assertTrue(playerSessionDto.getPosition() > 0, "PlayerSession positions are not greater than 0");
            assertEquals(playerSessionDto.getPosition(), playerSession.getPosition(), "PlayerSession positions do not match");

            // AppUser Assertions
            var appUserDto = playerSessionDto.getUser();
            var appUserId = appUserDto.getId();
            var appUserOpt = sqlClient.getAppUser(appUserId);
            assertTrue(appUserOpt.isPresent(), "AppUser not found for ID");
            var appUser = appUserOpt.get();
            assertEquals(appUserId, appUser.getId(), "AppUser ids do not match");
            assertEquals(appUserDto.getUsername(), appUser.getUsername(), "AppUser usernames do not match");
            assertEquals(appUserDto.getFirstName(), appUser.getFirstName(), "AppUser first names do not match");
            assertEquals(appUserDto.getLastName(), appUser.getLastName(), "AppUser last names do not match");
            assertEquals(appUserDto.getEmail(), appUser.getEmail(), "AppUser emails do not match");
            assertEquals(appUserDto.isEmailVerified(), appUser.isEmailVerified(), "AppUser emailVerified do not match");
            assertEquals(appUserDto.isEnabled(), appUser.isEnabled(), "AppUser enabled do not match");
            assertTrue(appUser.isEnabled(), "AppUser enabled is not true");

            // PokerTable Assertions
            var tableDto = playerSessionDto.getPokerTable();
            var tableId = tableDto.getId();
            var tableOpt = sqlClient.getPokerTable(tableId);
            assertTrue(tableOpt.isPresent(), "PokerTable not found for ID");
            var table = tableOpt.get();
            assertEquals(tableId, table.getId(), "PokerTable ids do not match");
            assertEquals(tableDto.getName(), table.getName(), "PokerTable names do not match");
            assertEquals(tableDto.getGameType(), table.getGameType(), "PokerTable gameTypes do not match");
        });
    }

    private void assertPlayersSubscribed(List<ServerMessageDTO> listenerMessages) {
        listenerMessages.stream()
                .filter(message -> message.getType() == ServerMessageType.PLAYER_SUBSCRIBED)
                .forEach(message -> {
                    var payload = (PlayerSubscribedDTO) message.getPayload();

                });
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

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
