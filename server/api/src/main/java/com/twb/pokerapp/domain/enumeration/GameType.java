package com.twb.pokerapp.domain.enumeration;

import com.twb.pokerapp.service.game.thread.GamePlayerActionService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.blackjack.BlackjackGameThread;
import com.twb.pokerapp.service.game.thread.impl.blackjack.BlackjackPlayerActionService;
import com.twb.pokerapp.service.game.thread.impl.texas.TexasGameThread;
import com.twb.pokerapp.service.game.thread.impl.texas.TexasPlayerActionService;
import com.twb.pokerapp.service.table.validation.TableValidationService;
import com.twb.pokerapp.service.table.validation.impl.BlackjackTableValidationService;
import com.twb.pokerapp.service.table.validation.impl.TexasTableValidationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

@Getter
@RequiredArgsConstructor
public enum GameType {
    TEXAS_HOLDEM(2, 8) {
        @Override
        public TableValidationService getValidationService(ApplicationContext context) {
            return context.getBean(TexasTableValidationService.class);
        }

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
        public TableValidationService getValidationService(ApplicationContext context) {
            return context.getBean(BlackjackTableValidationService.class);
        }

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

    public abstract TableValidationService getValidationService(ApplicationContext context);

    public abstract GameThread getGameThread(ApplicationContext context, GameThreadParams params);

    public abstract GamePlayerActionService getPlayerActionService(ApplicationContext context);
}
