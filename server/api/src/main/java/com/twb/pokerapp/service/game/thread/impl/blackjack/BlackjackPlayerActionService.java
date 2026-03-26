package com.twb.pokerapp.service.game.thread.impl.blackjack;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.service.game.thread.GamePlayerActionService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("blackJackPlayerActionService")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BlackjackPlayerActionService extends GamePlayerActionService {

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @Override
    public PlayerAction onPlayerAction(PlayerSession playerSession, BettingRound bettingRound, GameThread gameThread, CreatePlayerActionDTO createDto) {
        throw new NotImplementedException("Blackjack player actions not implemented yet");
    }
}
