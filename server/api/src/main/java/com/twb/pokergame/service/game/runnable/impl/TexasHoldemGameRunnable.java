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
        super(pokerTableId);
    }

    @Override
    public void run() {
        System.out.println("pokerTableId = " + pokerTableId);

        for (int index = 0; index < 3000; index++) {

            System.out.println("index = " + index);
            sendLogMessage(String.format("Texas Holdem Game Runnable %s with index %d", pokerTableId.toString(), index));

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
