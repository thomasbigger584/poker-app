package com.twb.pokerapp.utils.validator;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.dto.appuser.AppUserDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.dto.pokertable.TableDTO;
import com.twb.pokerapp.utils.http.message.PlayersServerMessages;
import com.twb.pokerapp.utils.sql.SqlClient;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerConnectedDTO;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public abstract class Validator {
    protected final SqlClient sqlClient;

    public void validateEndOfRun(PlayersServerMessages messages) {
        messages.getListenerMessages().stream()
                .filter(message -> message.getType() == ServerMessageType.PLAYER_CONNECTED)
                .forEach(message -> {
                    PlayerConnectedDTO payload = (PlayerConnectedDTO) message.getPayload();

                    // PlayerSession Assertions
                    PlayerSessionDTO playerSessionDto = payload.getPlayerSession();
                    assertEquals(SessionState.CONNECTED, playerSessionDto.getSessionState(), "PlayerSession state should be CONNECTED");

                    UUID playerSessionId = playerSessionDto.getId();
                    Optional<PlayerSession> playerSessionOpt = sqlClient.getPlayerSession(playerSessionId);
                    assertTrue(playerSessionOpt.isPresent(), "PlayerSession not found for ID");
                    PlayerSession playerSession = playerSessionOpt.get();
                    assertEquals(playerSessionId, playerSession.getId(), "PlayerSession IDs do not match");
                    assertTrue(playerSessionDto.getPosition() > 0, "PlayerSession positions are not greater than 0");
                    assertEquals(playerSessionDto.getPosition(), playerSession.getPosition(), "PlayerSession positions do not match");

                    // AppUser Assertions
                    AppUserDTO appUserDto = playerSessionDto.getUser();
                    UUID appUserId = appUserDto.getId();
                    Optional<AppUser> appUserOpt = sqlClient.getAppUser(appUserId);
                    assertTrue(appUserOpt.isPresent(), "AppUser not found for ID");
                    AppUser appUser = appUserOpt.get();
                    assertEquals(appUserId, appUser.getId(), "AppUser ids do not match");
                    assertEquals(appUserDto.getUsername(), appUser.getUsername(), "AppUser usernames do not match");
                    assertEquals(appUserDto.getFirstName(), appUser.getFirstName(), "AppUser first names do not match");
                    assertEquals(appUserDto.getLastName(), appUser.getLastName(), "AppUser last names do not match");
                    assertEquals(appUserDto.getEmail(), appUser.getEmail(), "AppUser emails do not match");
                    assertEquals(appUserDto.isEmailVerified(), appUser.isEmailVerified(), "AppUser emailVerified do not match");
                    assertEquals(appUserDto.isEnabled(), appUser.isEnabled(), "AppUser enabled do not match");
                    assertTrue(appUser.isEnabled(), "AppUser enabled is not true");

                    // PokerTable Assertions
                    TableDTO pokerTableDto = playerSessionDto.getPokerTable();
                    UUID pokerTableId = pokerTableDto.getId();
                    Optional<PokerTable> pokerTableOpt = sqlClient.getPokerTable(pokerTableId);
                    assertTrue(pokerTableOpt.isPresent(), "PokerTable not found for ID");
                    PokerTable pokerTable = pokerTableOpt.get();
                    assertEquals(pokerTableId, pokerTable.getId(), "PokerTable ids do not match");
                    assertEquals(pokerTableDto.getName(), pokerTable.getName(), "PokerTable names do not match");
                    assertEquals(pokerTableDto.getGameType(), pokerTable.getGameType(), "PokerTable gameTypes do not match");
                });

        // todo: add PLAYER_SUBSCRIBED validation

        onValidateEndOfRun(messages);
    }

    protected abstract void onValidateEndOfRun(PlayersServerMessages messages);
}
