package com.twb.pokergame.configuration.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class WebSocketConfiguration {

    @Bean
    public TaskScheduler heartBeatScheduler() {
        // required to get a valid response from heartbeat
        return new ThreadPoolTaskScheduler();
    }
}
