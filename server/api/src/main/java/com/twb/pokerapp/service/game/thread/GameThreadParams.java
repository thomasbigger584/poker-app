package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.enumeration.GameType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Getter
@Builder
public class GameThreadParams {
    private GameType gameType;
    private UUID tableId;
    private CountDownLatch startLatch;
    private long dealWaitMs;
    private long dbPollWaitMs;
    private long evalWaitMs;
    private long playerTurnWaitMs;
}
