package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;

public interface GamePlayerActionService {
    boolean playerAction(PlayerSession playerSession,
                         GameThread gameThread,
                         CreatePlayerActionDTO createDto);
}
