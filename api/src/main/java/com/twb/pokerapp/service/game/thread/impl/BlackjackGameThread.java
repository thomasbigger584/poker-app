package com.twb.pokerapp.service.game.thread.impl;

import com.twb.pokerapp.domain.enumeration.RoundState;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public final class BlackjackGameThread extends GameThread {
    private static final Logger logger =
            LoggerFactory.getLogger(BlackjackGameThread.class);

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
    protected RoundState getNextRoundState(RoundState roundState) {
        throw new NotImplementedException("Blackjack not implemented yet");
    }
}
