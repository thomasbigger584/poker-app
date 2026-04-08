package com.twb.pokerapp.data.exception;

import java.io.IOException;

public class UnauthorizedException extends IOException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
