package com.twb.pokerapp.service.game.thread.impl.blackjack;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.service.game.thread.GamePlayerActionService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("blackJackPlayerActionService")
public class BlackjackPlayerActionService implements GamePlayerActionService {
    @Override
    public boolean playerAction(PokerTable table, PlayerSession playerSession, GameThread gameThread, CreatePlayerActionDTO createDto) {
        throw new NotImplementedException("Blackjack player actions not implemented yet");
    }

}
