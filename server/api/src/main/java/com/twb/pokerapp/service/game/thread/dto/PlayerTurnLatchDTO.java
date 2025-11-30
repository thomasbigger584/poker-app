package com.twb.pokerapp.service.game.thread.dto;

import com.twb.pokerapp.domain.PlayerSession;

import java.util.concurrent.CountDownLatch;

public record PlayerTurnLatchDTO(PlayerSession playerSession, CountDownLatch playerTurnLatch) {
    public static PlayerTurnLatchDTO of(PlayerSession playerSession) {
        return new PlayerTurnLatchDTO(playerSession, new CountDownLatch(1));
    }

    public void countDown() {
        playerTurnLatch.countDown();
    }
}
