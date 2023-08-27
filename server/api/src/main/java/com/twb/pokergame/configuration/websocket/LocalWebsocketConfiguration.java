package com.twb.pokergame.configuration.websocket;

import com.twb.pokergame.configuration.ProfileConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.websocket.stream-bytes-limit:524288}") // 512 * 1024
    private int streamBytesLimit;

    @Value("${app.websocket.http-message-cache-size:1000}")
    private int httpMessageCacheSize;

    @Value("${app.websocket.disconnect-delay:30000}") //30 * 1000
    private long disconnectDelayMs;

    @Autowired
    private TaskScheduler taskScheduler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // - /app used for MessageMapping
        // - /topic used for SubscribeMapping
        //     (client connects directly to topic so we wait to forward this into application)
        registry.setApplicationDestinationPrefixes("/app", "/topic");
        registry.enableSimpleBroker("/topic")
                .setTaskScheduler(taskScheduler);
        registry.setPreservePublishOrder(true);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/looping")
                .withSockJS()
                .setStreamBytesLimit(streamBytesLimit)
                .setHttpMessageCacheSize(httpMessageCacheSize)
                .setDisconnectDelay(disconnectDelayMs);
    }
}