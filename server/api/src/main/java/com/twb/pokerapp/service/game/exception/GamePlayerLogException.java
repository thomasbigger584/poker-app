package com.twb.pokerapp.service.game.exception;


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
