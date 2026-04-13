package com.twb.pokerapp.configuration.websocket;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@EnableRabbit
@Configuration
@EnableWebSocketMessageBroker
public class RabbitMqConfiguration implements WebSocketMessageBrokerConfigurer {

    @Value("${app.relay.host:rabbitmq}")
    private String relayHost;

    @Value("${app.relay.port:61613}")
    private int relayPort;

    @Value("${app.relay.virtual-host:/}")
    private String virtualHost;

    @Value("${app.relay.client.login:admin}")
    private String clientLogin;

    @Value("${app.relay.client.passcode:admin}")
    private String clientPasscode;

    @Value("${app.relay.system.login:admin}")
    private String systemLogin;

    @Value("${app.relay.system.passcode:admin}")
    private String systemPasscode;

    @Value("${app.websocket.heartbeat.thread-pool-size:4}")
    private int heartbeatThreadPoolSize;

    @Value("${app.websocket.stream-limit-mb:2}")
    private int streamLimitMb;

    @Value("${app.websocket.http-message-cache-size:1000}")
    private int httpMessageCacheSize;

    @Value("${app.websocket.disconnect-delay-secs:30}")
    private long disconnectDelaySecs;

    @Value("${app.websocket.message-size-limit-kb:256}")
    private int messageSizeLimitKb;

    @Value("${app.websocket.send-buffer-size-limit-mb:1}")
    private int sendBufferSizeLimitMb;

    @Value("${app.websocket.send-time-limit-secs:5}")
    private int sendTimeLimitSecs;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app", "/topic")
                .setUserDestinationPrefix("/user");

        registry.enableStompBrokerRelay("/topic", "/user")
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
                .setStreamBytesLimit(streamLimitMb * 1024 * 1024)
                .setHttpMessageCacheSize(httpMessageCacheSize)
                .setDisconnectDelay(disconnectDelaySecs * 1000);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(messageSizeLimitKb * 1024);
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
