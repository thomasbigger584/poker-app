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

@Configuration
@EnableWebSocketMessageBroker
@Profile(ProfileConfiguration.DIGITALOCEAN_PROFILE)
public class RabbitMqWebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Value("${app.relay.host:rabbitmq}")
    private String relayHost;

    @Value("${app.relay.port:61613}")
    private int relayPort;

    @Value("${app.relay.virtualHost:/}")
    private String virtualHost;

    @Value("${app.client.login:admin}")
    private String clientLogin;

    @Value("${app.client.passcode:admin}")
    private String clientPasscode;

    @Value("${app.system.login:admin}")
    private String systemLogin;

    @Value("${app.system.passcode:admin}")
    private String systemPasscode;

    @Value("${app.cors.allow-origins}")
    private String allowOrigins;

    @Value("${app.websocket.stream-bytes-limit:524288}") // 512 * 1024
    private int streamBytesLimit;

    @Value("${app.websocket.http-message-cache-size:1000}")
    private int httpMessageCacheSize;

    @Value("${app.websocket.disconnect-delay:30000}") //30 * 1000
    private long disconnectDelayMs;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // - /app used for MessageMapping
        // - /topic used for SubscribeMapping
        //     (client connects directly to topic so we wait to forward this into application)
        registry.setApplicationDestinationPrefixes("/app", "/topic");
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort)
                .setVirtualHost(virtualHost)
                .setClientLogin(clientLogin)
                .setClientPasscode(clientPasscode)
                .setSystemLogin(systemLogin)
                .setSystemPasscode(systemPasscode)
                .setTaskScheduler(heartBeatScheduler());
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

    @Bean
    public TaskScheduler heartBeatScheduler() {
        // required to get a valid response from heartbeat
        return new ThreadPoolTaskScheduler();
    }
}
