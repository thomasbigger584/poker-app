package com.twb.pokerapp.service.game.thread.impl.texas.bettinground;

import com.twb.pokerapp.domain.enumeration.GameType;
import com.twb.pokerapp.service.game.thread.GameThread;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TexasBettingRoundService {
    private final ApplicationContext context;

    public void runBettingRound(GameThread gameThread) {
        var service = GameType.TEXAS_HOLDEM
                .getPlayerTurnService(context, gameThread);
        try {
            boolean shouldContinue;
            do {
                shouldContinue = service.executeTurn(gameThread);
            } while (shouldContinue);
        } finally {
            service.finish();
        }
    }
}
