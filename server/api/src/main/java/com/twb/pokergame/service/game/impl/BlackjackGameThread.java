package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.service.game.GameThread;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public final class BlackjackGameThread extends GameThread {
    private static final Logger logger =
            LoggerFactory.getLogger(BlackjackGameThread.class);

    public BlackjackGameThread(UUID tableId) {
        super(tableId);
    }

    @Override
    protected void onRun() {
        throw new NotImplementedException("Blackjack not implemented yet");
    }
}
