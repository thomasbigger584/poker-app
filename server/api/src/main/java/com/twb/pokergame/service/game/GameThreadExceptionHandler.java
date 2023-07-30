package com.twb.pokergame.service.game;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameThreadExceptionHandler.class);

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (thread instanceof GameThread gameThread) {
            gameThread.fail(throwable.getMessage());
            logger.error(throwable.getMessage(), throwable);
        } else {
            logger.error(throwable.getMessage()
                    + " (Wasn't thrown in GameThread)", throwable);
        }
    }
}
