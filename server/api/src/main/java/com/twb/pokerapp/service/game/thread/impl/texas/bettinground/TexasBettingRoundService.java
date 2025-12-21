package com.twb.pokerapp.service.game.thread.impl.texas.bettinground;

import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.texas.bettinground.LastAggressorService.LastAggressorBreakException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TexasBettingRoundService {
    private final ApplicationContext context;

    public void runBettingRound(GameThreadParams params, GameThread gameThread) {
        var lastAggressorService = context.getBean(LastAggressorService.class, params, gameThread);
        while (true) {
            try {
                lastAggressorService.prePlayerTurn();
                gameThread.checkRoundInterrupted();
                lastAggressorService.waitPlayerTurn();
                lastAggressorService.postPlayerTurn();
            } catch (LastAggressorBreakException e) {
                log.info("Breaking Betting loop Last Aggressor: {}", e.getMessage());
                break;
            }
        }
        lastAggressorService.finishBettingRound();
    }
}
