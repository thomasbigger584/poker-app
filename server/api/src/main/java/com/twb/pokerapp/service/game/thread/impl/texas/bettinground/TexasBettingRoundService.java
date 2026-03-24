package com.twb.pokerapp.service.game.thread.impl.texas.bettinground;

import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.texas.bettinground.TexasLastAggressorService.LastAggressorBreakException;
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
        var service = context.getBean(TexasLastAggressorService.class, params, gameThread);
        try {
            runBettingRound(gameThread, service);
        } finally {
            service.finishBettingRound();
        }
    }

    private void runBettingRound(GameThread gameThread, TexasLastAggressorService service) {
        while (true) {
            try {
                service.runPlayerInBettingRound(gameThread);
            } catch (LastAggressorBreakException e) {
                log.info("Breaking Betting loop Last Aggressor: {}", e.getMessage());
                break;
            }
        }
    }
}
