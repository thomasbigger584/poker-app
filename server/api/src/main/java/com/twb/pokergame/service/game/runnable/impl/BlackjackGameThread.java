package com.twb.pokergame.service.game.runnable.impl;

import com.twb.pokergame.service.game.runnable.GameThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class BlackjackGameThread extends GameThread {
    private static final Logger logger = LoggerFactory.getLogger(BlackjackGameThread.class);

    public BlackjackGameThread(UUID tableId) {
        super(tableId);
    }

    @Override
    protected void onRun() {
        logger.info("BlackjackGameRunnable.onRun");
       sleepInMs(10000);
    }
}
