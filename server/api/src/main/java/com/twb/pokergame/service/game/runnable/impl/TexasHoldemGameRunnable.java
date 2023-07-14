package com.twb.pokergame.service.game.runnable.impl;

import com.twb.pokergame.service.game.runnable.GameRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class TexasHoldemGameRunnable extends GameRunnable {
    private static final Logger logger = LoggerFactory.getLogger(TexasHoldemGameRunnable.class);

    public TexasHoldemGameRunnable(UUID pokerTableId) {
        super(pokerTableId, 2);
    }

    @Override
    protected void onRun() {
        System.out.println("TexasHoldemGameRunnable.onRun");
        sleep(1000);
    }
}
