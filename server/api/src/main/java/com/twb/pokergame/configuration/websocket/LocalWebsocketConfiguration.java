package com.twb.pokergame.configuration.websocket;

import com.twb.pokergame.configuration.ProfileConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Profile(ProfileConfiguration.LOCAL_PROFILE)
public class LocalWebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // - /app used for MessageMapping
        // - /topic used for SubscribeMapping
        //     (client connects directly to topic so we wait to forward this into application)
        registry.setApplicationDestinationPrefixes("/app", "/topic");
        registry.enableSimpleBroker("/topic")
                .setTaskScheduler(heartBeatScheduler());
        registry.setPreservePublishOrder(true);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/looping")
                .withSockJS();
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        // required to get a valid response from heartbeat
        return new ThreadPoolTaskScheduler();
    }
}