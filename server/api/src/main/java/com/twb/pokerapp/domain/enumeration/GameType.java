package com.twb.pokerapp.domain.enumeration;

import com.twb.pokerapp.service.game.thread.GamePlayerActionService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.blackjack.BlackjackGameThread;
import com.twb.pokerapp.service.game.thread.impl.blackjack.BlackjackPlayerActionService;
import com.twb.pokerapp.service.game.thread.impl.texas.TexasGameThread;
import com.twb.pokerapp.service.game.thread.impl.texas.TexasPlayerActionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

@Getter
@RequiredArgsConstructor
public enum GameType {
    TEXAS_HOLDEM(2, 8) {
        @Override
        public GameThread getGameThread(ApplicationContext context, GameThreadParams params) {
            return context.getBean(TexasGameThread.class, params);
        }

        @Override
        public GamePlayerActionService getPlayerActionService(ApplicationContext context) {
            return context.getBean(TexasPlayerActionService.class);
        }
    },
    BLACKJACK(1, 1) {
        @Override
        public GameThread getGameThread(ApplicationContext context, GameThreadParams params) {
            return context.getBean(BlackjackGameThread.class, params);
        }

        @Override
        public GamePlayerActionService getPlayerActionService(ApplicationContext context) {
            return context.getBean(BlackjackPlayerActionService.class);
        }
    };

    private final int minPlayerCount;
    private final int maxPlayerCount;

    public abstract GameThread getGameThread(ApplicationContext context, GameThreadParams params);

    public abstract GamePlayerActionService getPlayerActionService(ApplicationContext context);
}
