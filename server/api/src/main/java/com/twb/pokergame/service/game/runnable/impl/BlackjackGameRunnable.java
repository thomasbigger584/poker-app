package com.twb.pokergame.service.game.runnable.impl;

import com.twb.pokergame.service.game.runnable.GameRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class BlackjackGameRunnable extends GameRunnable {
    private static final Logger logger = LoggerFactory.getLogger(BlackjackGameRunnable.class);
    private static final int MIN_NUMBER_OF_PLAYERS = 1;

    public BlackjackGameRunnable(UUID pokerTableId) {
        super(pokerTableId);
    }

    @Override
    protected void onRun() {
        System.out.println("BlackjackGameRunnable.onRun");
        sleep(1000);
    }

    @Override
    protected int getMinNumberOfPlayers() {
        return MIN_NUMBER_OF_PLAYERS;
    }
}
