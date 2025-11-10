package com.twb.pokerapp.utils.validator.impl;

import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.utils.http.message.PlayersServerMessages;
import com.twb.pokerapp.utils.sql.SqlClient;
import com.twb.pokerapp.utils.validator.Validator;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageType;
import com.twb.pokerapp.web.websocket.message.server.payload.DealerDeterminedDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TexasHoldemValidator extends Validator {

    public TexasHoldemValidator(SqlClient sqlClient) {
        super(sqlClient);
    }

    @Override
    protected void onValidateEndOfRun(PlayersServerMessages messages) {
        messages.getListenerMessages().stream()
                .filter(message -> message.getType() == ServerMessageType.DEALER_DETERMINED)
                .forEach(message -> {
                    var payload = (DealerDeterminedDTO) message.getPayload();

                    // PlayerSession Assertions
                    var playerSessionDto = payload.getPlayerSession();
                    assertEquals(SessionState.CONNECTED, playerSessionDto.getSessionState(), "PlayerSession state should be CONNECTED");
                    assertTrue(playerSessionDto.getDealer(), "PlayerSession should be marked as dealer");

                    var playerSessionId = playerSessionDto.getId();
                    var playerSessionOpt = sqlClient.getPlayerSession(playerSessionId);
                    assertTrue(playerSessionOpt.isPresent(), "PlayerSession not found for ID");
                    var playerSession = playerSessionOpt.get();
                    assertEquals(playerSessionId, playerSession.getId(), "PlayerSession IDs do not match");
                    assertTrue(playerSessionDto.getPosition() > 0, "PlayerSession positions are not greater than 0");
                    assertEquals(playerSessionDto.getPosition(), playerSession.getPosition(), "PlayerSession positions do not match");
                });


    }
}
