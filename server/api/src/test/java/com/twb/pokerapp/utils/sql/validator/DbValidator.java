package com.twb.pokerapp.utils.sql.validator;

import com.twb.pokerapp.domain.PlayerSession;
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
                    assertTrue(playerSessionOpt.isPresent(), "Player Session not found for id: " + playerSessionId);
                    PlayerSession playerSession = playerSessionOpt.get();

                    assertEquals(playerSessionDto.getId(), playerSession.getId());




                });
        onValidateEndOfRun(messages);
    }

    protected abstract void onValidateEndOfRun(PlayersServerMessages messages);
}
