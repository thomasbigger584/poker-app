package com.twb.pokerapp.exception.game;


import com.twb.pokerapp.domain.PlayerSession;
import lombok.Getter;

public class GamePlayerLogException extends RuntimeException {

    @Getter
    private final PlayerSession playerSession;

    public GamePlayerLogException(PlayerSession playerSession, String message) {
        super(message);
        this.playerSession = playerSession;
    }
}
