package com.twb.pokerapp.utils.game.turn;

import com.twb.pokerapp.utils.game.player.AbstractTestUser;
import com.twb.pokerapp.web.websocket.message.server.payload.PlayerTurnDTO;
import org.springframework.messaging.simp.stomp.StompHeaders;

public interface TurnHandler {

    void handle(AbstractTestUser user, StompHeaders headers, PlayerTurnDTO playerTurn);
}