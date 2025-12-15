package com.twb.pokerapp.service.idepetency;

import com.twb.pokerapp.domain.enumeration.ActionType;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.twb.pokerapp.configuration.CacheConfiguration.IDEMPOTENCY_ACTION_CACHE_NAME;

@Component
public class IdempotencyService {
    public static final String PLAYER_ACTION_KEY_GEN = "playerActionKeyGenerator";

    @Cacheable(value = IDEMPOTENCY_ACTION_CACHE_NAME, keyGenerator = PLAYER_ACTION_KEY_GEN)
    public boolean isActionIdempotent(UUID playerSessionId, UUID bettingRoundId, ActionType actionType) {
        return false;
    }

    @CachePut(value = IDEMPOTENCY_ACTION_CACHE_NAME, keyGenerator = PLAYER_ACTION_KEY_GEN)
    public boolean recordAction(UUID playerSessionId, UUID bettingRoundId, ActionType actionType) {
        return true;
    }
}