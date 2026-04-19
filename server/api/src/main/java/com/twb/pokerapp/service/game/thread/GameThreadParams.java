package com.twb.pokerapp.service.game.thread;

import com.twb.pokerapp.domain.PokerTable;
import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.CountDownLatch;

@Getter
@Builder
public class GameThreadParams {
    private PokerTable table;
    private CountDownLatch startLatch;
    private CountDownLatch endLatch;
    private long dealWaitMs;
    private long dbPollWaitMs;
    private long evalWaitMs;
    private long playerTurnWaitMs;
    private long roundStartWaitMs;
    private long roundEndWaitMs;
}
