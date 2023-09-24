package com.twb.pokergame.service.game.thread;

import com.twb.pokergame.domain.enumeration.GameType;
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
}
