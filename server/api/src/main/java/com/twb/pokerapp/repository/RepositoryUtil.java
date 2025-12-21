package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.exception.game.GameInterruptedException;
import com.twb.pokerapp.exception.game.GamePlayerErrorLogException;
import com.twb.pokerapp.exception.game.GamePlayerLogException;

import java.util.Optional;


@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class RepositoryUtil {
    public static <T> T getThrowPlayerLog(Optional<T> optional, PlayerSession playerSession, String message) {
        return optional.orElseThrow(() -> new GamePlayerLogException(playerSession, message));
    }

    public static <T> T getThrowPlayerErrorLog(Optional<T> optional, PlayerSession playerSession, String message) {
        return optional.orElseThrow(() -> new GamePlayerErrorLogException(playerSession, message));
    }

    public static <T> T getThrowPlayerErrorLog(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new GamePlayerErrorLogException(message));
    }

    public static <T> T getThrowGameInterrupted(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new GameInterruptedException(message));
    }
}
