package com.twb.pokerapp.exception.game;

public class RoundInterruptedException extends RuntimeException {

    public RoundInterruptedException(String message) {
        super(message);
    }

    public RoundInterruptedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
