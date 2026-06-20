package com.twb.pokerapp.configuration.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

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
         *
         * Native WebSocket (no SockJS): SockJS is a text-only transport and would force the
         * protobuf payload into base64/text framing. A native endpoint lets STOMP frames carry
         * raw binary protobuf bodies.
         */
        registry.addEndpoint("/looping")
                .setAllowedOriginPatterns(allowedOrigins.split(","));
    }

    /**
     * Registers the binary-protobuf converter for STOMP payloads (inbound {@code @Payload} parsing
     * and outbound {@code convertAndSend}), keeping the default converters as well.
     */
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(0, new ProtobufMessageConverter());
        return true;
    }
}
