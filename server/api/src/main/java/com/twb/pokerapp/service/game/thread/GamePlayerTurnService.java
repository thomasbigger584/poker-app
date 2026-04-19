package com.twb.pokerapp.service.game.thread;

public interface GamePlayerTurnService {
    boolean executeTurn(GameThread gameThread);

    void finish();
}
