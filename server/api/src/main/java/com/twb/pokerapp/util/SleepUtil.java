package com.twb.pokerapp.util;

public class SleepUtil {

    public static void sleepInMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to sleep for " + ms + "ms", e);
        }
    }
}
