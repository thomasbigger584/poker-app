package com.twb.pokerapp.utils.game;

import java.util.concurrent.CountDownLatch;

public record GameLatches(
        CountDownLatch roundLatch,
        CountDownLatch gameLatch
) {
    private static final int SINGLE = 1;

    public static GameLatches create() {
        CountDownLatch roundLatch = new CountDownLatch(SINGLE);
        CountDownLatch gameLatch = new CountDownLatch(SINGLE);
        return new GameLatches(roundLatch, gameLatch);
    }
}