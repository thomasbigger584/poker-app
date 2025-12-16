package com.twb.pokerapp.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {
    private static final int IDEMPOTENCY_ACTION_MAX_SIZE = 100_000;
    private static final long IDEMPOTENCY_ACTION_TTL_SECS = 2;
    public static final String IDEMPOTENCY_ACTION_CACHE_NAME = "idempotency-actions";

    @Bean
    public CaffeineCache idempotencyCache() {
        var caffeineBuilder = Caffeine.newBuilder()
                .expireAfterWrite(IDEMPOTENCY_ACTION_TTL_SECS, TimeUnit.SECONDS)
                .maximumSize(IDEMPOTENCY_ACTION_MAX_SIZE)
                .build();
        return new CaffeineCache(IDEMPOTENCY_ACTION_CACHE_NAME, caffeineBuilder);
    }

    @Bean
    public CacheManager cacheManager(CaffeineCache idempotencyCache) {
        var cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Collections.singletonList(idempotencyCache));
        return cacheManager;
    }
}
