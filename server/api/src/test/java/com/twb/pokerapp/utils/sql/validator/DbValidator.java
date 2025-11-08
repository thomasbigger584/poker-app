package com.twb.pokerapp.utils.sql.validator;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.dto.appuser.AppUserDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
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
public abstract class DbValidator {
    protected final SqlClient sqlClient;

    public void validateEndOfRun(PlayersServerMessages messages) {
        messages.getListenerMessages().stream()
                .filter(message -> message.getType() == ServerMessageType.PLAYER_CONNECTED)
                .forEach(message -> {
                    PlayerConnectedDTO payload = (PlayerConnectedDTO) message.getPayload();
                    PlayerSessionDTO playerSessionDto = payload.getPlayerSession();

                    UUID playerSessionId = playerSessionDto.getId();
                    Optional<PlayerSession> playerSessionOpt = sqlClient.getPlayerSession(playerSessionId);
                    assertTrue(playerSessionOpt.isPresent(), "PlayerSession not found for ID");
                    PlayerSession playerSession = playerSessionOpt.get();
                    assertEquals(playerSessionId, playerSession.getId(), "PlayerSession IDs do not match");

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


                });

        onValidateEndOfRun(messages);
    }

    protected abstract void onValidateEndOfRun(PlayersServerMessages messages);
}
