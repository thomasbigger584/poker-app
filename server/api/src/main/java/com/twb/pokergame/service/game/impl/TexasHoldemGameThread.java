package com.twb.pokergame.service.game.impl;

import com.twb.pokergame.service.game.GameThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class TexasHoldemGameThread extends GameThread {
    private static final Logger logger = LoggerFactory.getLogger(TexasHoldemGameThread.class);

    public TexasHoldemGameThread(UUID tableId) {
        super(tableId);
    }

    @Override
    protected void onRun() {
        logger.info("TexasHoldemGameRunnable.onRun");
        sleepInMs(10000);
    }
}
