package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;

public interface GamePlayerActionService {
    boolean playerAction(PokerTable table,
                         PlayerSession playerSession,
                         GameThread gameThread,
                         CreatePlayerActionDTO createDto);
}
