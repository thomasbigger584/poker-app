package com.twb.pokerapp.configuration.websocket;

import com.twb.pokerapp.configuration.ProfileConfiguration;
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
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@Profile(ProfileConfiguration.LOCAL_PROFILE)
public class LocalWebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Value("${app.websocket.heartbeat.time-secs:5}")
    private int heartbeatTimeSecs;

    @Value("${app.websocket.heartbeat.thread-pool-size:2}")
    private int heartbeatThreadPoolSize;

    @Value("${app.websocket.stream-bytes-limit:524288}") // 512 * 1024
    private int streamBytesLimit;

    @Value("${app.websocket.http-message-cache-size:1000}")
    private int httpMessageCacheSize;

    @Value("${app.websocket.disconnect-delay:30000}") //30 * 1000
    private long disconnectDelayMs;

    @Value("${app.websocket.message-size-limit-mb:1}")
    private int messageSizeLimitMb;

    @Value("${app.websocket.send-buffer-size-limit-mb:2}")
    private int sendBufferSizeLimitMb;

    @Value("${app.websocket.send-time-limit-secs:30}")
    private int sendTimeLimitSecs;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // - /app used for MessageMapping
        // - /topic used for SubscribeMapping
        // - /user/{username}/{destination} for users to receive specific notifications
        //     (client connects directly to topic so we wait to forward this into application)
        var heartbeatMs = heartbeatTimeSecs * 1000L;
        registry.setApplicationDestinationPrefixes("/app", "/topic")
                .setUserDestinationPrefix("/user")
                .setPreservePublishOrder(true)
                .enableSimpleBroker("/topic", "/user")
                .setHeartbeatValue(new long[]{heartbeatMs, heartbeatMs * 2})
                .setTaskScheduler(heartBeatScheduler());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/looping")
                .withSockJS()
                .setStreamBytesLimit(streamBytesLimit)
                .setHttpMessageCacheSize(httpMessageCacheSize)
                .setDisconnectDelay(disconnectDelayMs);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(messageSizeLimitMb * 1024 * 1024);
        registry.setSendBufferSizeLimit(sendBufferSizeLimitMb * 1024 * 1024);
        registry.setSendTimeLimit(sendTimeLimitSecs * 1000);
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(heartbeatThreadPoolSize);
        scheduler.setThreadNamePrefix("ws-heartbeat-thread-");
        scheduler.initialize();
        return scheduler;
    }
}
