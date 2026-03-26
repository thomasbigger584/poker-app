package com.twb.pokerapp.service.game.thread.impl.blackjack.bettinground;

import com.twb.pokerapp.service.game.thread.GamePlayerTurnService;
import com.twb.pokerapp.service.game.thread.GameThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component("blackjackPlayerTurnService")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BlackjackPlayerTurnService implements GamePlayerTurnService {

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @Override
    public boolean executeTurn(GameThread gameThread) {
        throw new NotImplementedException("Blackjack player turns not implemented yet");
    }

    @Override
    public void finish() {
        throw new NotImplementedException("Blackjack player turns not implemented yet");
    }
}
