package com.twb.pokerapp.domain.enumeration;

import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.BlackjackGameThread;
import com.twb.pokerapp.service.game.thread.impl.TexasHoldemGameThread;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.ApplicationContext;

@Getter
@ToString
@RequiredArgsConstructor
public enum GameType {
    TEXAS_HOLDEM(2, 6) {
        @Override
        public GameThread getGameThread(ApplicationContext context, GameThreadParams params) {
            return context.getBean(TexasHoldemGameThread.class, params);
        }
    },
    BLACKJACK(1, 1) {
        @Override
        public GameThread getGameThread(ApplicationContext context, GameThreadParams params) {
            return context.getBean(BlackjackGameThread.class, params);
        }
    };

    private final int minPlayerCount;
    private final int maxPlayerCount;

    public abstract GameThread getGameThread(ApplicationContext context, GameThreadParams params);
}
