package com.twb.pokerapp.exception.game;


import com.twb.pokerapp.domain.PlayerSession;
import lombok.Getter;

public class GamePlayerErrorLogException extends RuntimeException {

    @Getter
    private final PlayerSession playerSession;

    public GamePlayerErrorLogException(String message) {
        super(message);
        this.playerSession = null;
    }

    public GamePlayerErrorLogException(PlayerSession playerSession, String message) {
        super(message);
        this.playerSession = playerSession;
    }
}
