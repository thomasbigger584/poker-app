package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.web.websocket.message.client.CreatePlayerActionDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class BlackjackGameThread extends GameThread {
    public BlackjackGameThread(GameThreadParams params) {
        super(params);
    }

    @Override
    protected void onInitRound() {
        throw new NotImplementedException("Blackjack not implemented yet");
    }

    @Override
    protected void onRunRound(RoundState roundState) {
        throw new NotImplementedException("Blackjack not implemented yet");
    }

    @Override
    protected void onPlayerAction(PlayerSession playerSession, CreatePlayerActionDTO createActionDto) {
        throw new NotImplementedException("Blackjack not implemented yet");
    }

    @Override
    protected RoundState getNextRoundState(RoundState roundState) {
        throw new NotImplementedException("Blackjack not implemented yet");
    }
}
