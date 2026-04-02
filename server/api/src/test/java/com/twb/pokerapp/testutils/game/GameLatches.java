package com.twb.pokerapp.testutils.game;

import java.util.concurrent.CountDownLatch;

public record GameLatches(
        CountDownLatch gameLatch
) {
    private static final int SINGLE = 1;

    public static GameLatches create() {
        var gameLatch = new CountDownLatch(SINGLE);
        return new GameLatches(gameLatch);
    }
}