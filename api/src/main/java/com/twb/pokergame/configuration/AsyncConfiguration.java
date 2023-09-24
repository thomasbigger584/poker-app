package com.twb.pokergame.configuration;

import com.antkorwin.xsync.XSync;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class AsyncConfiguration {

    @Bean
    public XSync<UUID> uuidMutex() {
        return new XSync<>();
    }
}
