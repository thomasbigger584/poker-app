package com.twb.pokerapp.service.game.exception;

public class RoundInterruptedException extends RuntimeException {

    public RoundInterruptedException(String message) {
        super(message);
    }

    public RoundInterruptedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
