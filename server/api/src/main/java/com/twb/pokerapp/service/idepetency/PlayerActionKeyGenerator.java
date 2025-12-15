package com.twb.pokerapp.service.idepetency;

import com.twb.pokerapp.domain.enumeration.ActionType;
import jakarta.annotation.Nonnull;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

import static com.twb.pokerapp.service.idepetency.IdempotencyService.PLAYER_ACTION_KEY_GEN;

@Component(PLAYER_ACTION_KEY_GEN)
public class PlayerActionKeyGenerator implements KeyGenerator {

    @Nonnull
    @Override
    public Object generate(@Nonnull Object target, @Nonnull Method method, Object... params) {
        var playerSessionId = (UUID) params[0];
        var bettingRoundId = (UUID) params[1];
        var actionType = (ActionType) params[2];
        return String.format("%s_%s_%s", playerSessionId, bettingRoundId, actionType);
    }
}