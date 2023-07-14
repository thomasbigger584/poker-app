package com.twb.pokergame.configuration;

import com.antkorwin.xsync.XSync;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;

@Configuration
public class AsyncConfiguration {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Bean
    public XSync<UUID> uuidMutex() {
        return new XSync<>();
    }
}
