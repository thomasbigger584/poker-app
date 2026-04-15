package com.twb.pokerapp.configuration.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class RabbitMqConfiguration implements WebSocketMessageBrokerConfigurer {
    @Value("${spring.rabbitmq.host:localhost}")
    private String relayHost;

    @Value("${spring.rabbitmq.stomp-port:61613}")
    private int relayPort;

    @Value("${spring.rabbitmq.username:admin}")
    private String login;

    @Value("${spring.rabbitmq.password:admin}")
    private String passcode;

    @Value("${app.websocket.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${app.websocket.stream-limit-mb:1}")
    private int streamLimitMb;

    @Value("${app.websocket.http-message-cache-size:1000}")
    private int httpMessageCacheSize;

    @Value("${app.websocket.disconnect-delay-secs:1}")
    private int disconnectDelaySecs;

    /**
     * Heartbeat Interval (20 seconds).
     * Synchronized with AbstractTestUser to prevent connection drops in Docker.
     */
    private static final long HEARTBEAT_MS = 20000;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
         * STOMP Broker Relay:
         * Proxies messages to the RabbitMQ STOMP plugin.
         */
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort)
                .setClientLogin(login)
                .setClientPasscode(passcode)
                .setSystemLogin(login)
                .setSystemPasscode(passcode)
                .setSystemHeartbeatSendInterval(HEARTBEAT_MS)
                .setSystemHeartbeatReceiveInterval(HEARTBEAT_MS);

        /*
         * Application Destination Prefix:
         * Routes messages to @MessageMapping and @SubscribeMapping in controllers.
         */
        registry.setApplicationDestinationPrefixes("/app");

        /*
         * User Destination Prefix:
         * Necessary for /user/queue/notifications (private STOMP messaging).
         */
        registry.setUserDestinationPrefix("/user");

        /*
         * Path Matcher:
         * Uses '.' instead of '/' to match RabbitMQ's topic routing style.
         */
        registry.setPathMatcher(new AntPathMatcher("."));
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /*
         * WebSocket Handshake Endpoint:
         * The initial connection URL used by the client (ws://host:port/looping).
         */
        registry.addEndpoint("/looping")
                .setAllowedOriginPatterns(allowedOrigins.split(","))
                .withSockJS()
                .setStreamBytesLimit(streamLimitMb * 1024 * 1024)
                .setHttpMessageCacheSize(httpMessageCacheSize)
                .setDisconnectDelay(disconnectDelaySecs * 1000);
    }
}
