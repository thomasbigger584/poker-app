package com.twb.pokerapp.utils.game.player;

import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.util.Arrays;

public class TurnHandler {

    public void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn) {
        Arrays.stream(playerTurn.getActions())
                .findFirst()
                .ifPresent(action -> {
                    CreatePlayerActionDTO createDto = new CreatePlayerActionDTO();
                    createDto.setAction(action);

                    user.sendPlayerAction(createDto);
                });
    }
}