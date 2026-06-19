package com.twb.pokerapp.service.game;

import com.twb.pokerapp.proto.GameType;
import com.twb.pokerapp.service.game.thread.GamePlayerActionService;
import com.twb.pokerapp.service.game.thread.GamePlayerTurnService;
import com.twb.pokerapp.service.game.thread.GameThread;
import com.twb.pokerapp.service.game.thread.GameThreadParams;
import com.twb.pokerapp.service.game.thread.impl.blackjack.BlackjackGameThread;
import com.twb.pokerapp.service.game.thread.impl.blackjack.BlackjackPlayerActionService;
import com.twb.pokerapp.service.game.thread.impl.blackjack.bettinground.BlackjackPlayerTurnService;
import com.twb.pokerapp.service.game.thread.impl.texas.TexasGameThread;
import com.twb.pokerapp.service.game.thread.impl.texas.TexasPlayerActionService;
import com.twb.pokerapp.service.game.thread.impl.texas.bettinground.TexasPlayerTurnService;
import com.twb.pokerapp.service.table.validation.TableValidationService;
import com.twb.pokerapp.service.table.validation.impl.BlackjackTableValidationService;
import com.twb.pokerapp.service.table.validation.impl.TexasTableValidationService;
import org.springframework.context.ApplicationContext;

/**
 * Resolves the per-variant game beans and player-count bounds for a {@link GameType}. This is the
 * strategy factory that previously lived as abstract methods on the {@code GameType} domain enum;
 * moving it out keeps the proto enum a pure value type. The game-logic beans are prototype/
 * parameterized, so they are fetched from the {@link ApplicationContext} per call.
 *
 * <p>To add a game variant, add a proto {@code GameType} value and a branch in each switch.
 */
public final class GameStrategies {

    private GameStrategies() {
    }

    public static int minPlayers(GameType gameType) {
        return switch (gameType) {
            case GAME_TYPE_TEXAS_HOLDEM -> 2;
            case GAME_TYPE_BLACKJACK -> 1;
            default -> throw unsupported(gameType);
        };
    }

    public static int maxPlayers(GameType gameType) {
        return switch (gameType) {
            case GAME_TYPE_TEXAS_HOLDEM -> 8;
            case GAME_TYPE_BLACKJACK -> 1;
            default -> throw unsupported(gameType);
        };
    }

    public static TableValidationService validationService(GameType gameType, ApplicationContext context) {
        return switch (gameType) {
            case GAME_TYPE_TEXAS_HOLDEM -> context.getBean(TexasTableValidationService.class);
            case GAME_TYPE_BLACKJACK -> context.getBean(BlackjackTableValidationService.class);
            default -> throw unsupported(gameType);
        };
    }

    public static GameThread gameThread(GameType gameType, ApplicationContext context, GameThreadParams params) {
        return switch (gameType) {
            case GAME_TYPE_TEXAS_HOLDEM -> context.getBean(TexasGameThread.class, params);
            case GAME_TYPE_BLACKJACK -> context.getBean(BlackjackGameThread.class, params);
            default -> throw unsupported(gameType);
        };
    }

    public static GamePlayerActionService playerActionService(GameType gameType, ApplicationContext context) {
        return switch (gameType) {
            case GAME_TYPE_TEXAS_HOLDEM -> context.getBean(TexasPlayerActionService.class);
            case GAME_TYPE_BLACKJACK -> context.getBean(BlackjackPlayerActionService.class);
            default -> throw unsupported(gameType);
        };
    }

    public static GamePlayerTurnService playerTurnService(GameType gameType, ApplicationContext context, GameThread gameThread) {
        return switch (gameType) {
            case GAME_TYPE_TEXAS_HOLDEM -> context.getBean(TexasPlayerTurnService.class, gameThread);
            case GAME_TYPE_BLACKJACK -> context.getBean(BlackjackPlayerTurnService.class, gameThread);
            default -> throw unsupported(gameType);
        };
    }

    private static IllegalArgumentException unsupported(GameType gameType) {
        return new IllegalArgumentException("Unsupported game type: " + gameType);
    }
}
