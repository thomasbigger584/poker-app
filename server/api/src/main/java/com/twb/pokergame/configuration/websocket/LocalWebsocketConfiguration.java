package com.twb.pokergame.configuration.websocket;

import com.twb.pokergame.configuration.ProfileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Profile(ProfileConfiguration.LOCAL_PROFILE)
public class LocalWebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(LocalWebsocketConfiguration.class);

    @Value("${app.cors.allow-origins}")
    private String allowOrigins;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/poker-app-ws")
                .setAllowedOrigins(allowOrigins)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                logger.info("s********************************************************");

                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null) {
                    logger.info("StompHeaderAccessor is null");
                }

                logger.info("WEBSOCKET - host: {}, command: {}", accessor.getHost(), accessor.getCommand());

                if (accessor.isHeartbeat()) {
                    logger.info("Message is heartbeat");
                }

                switch (accessor.getCommand()) {
                    case STOMP -> {

                    }
                    case CONNECT -> onConnected(accessor);
                    case DISCONNECT -> {
                    }
                    case SUBSCRIBE -> {
                    }
                    case UNSUBSCRIBE -> {
                    }
                    case SEND -> {
                    }
                    case ACK -> {
                    }
                    case NACK -> {
                    }
                    case BEGIN -> {
                    }
                    case COMMIT -> {
                    }
                    case ABORT -> {
                    }
                    case CONNECTED -> {
                    }
                    case RECEIPT -> {
                    }
                    case MESSAGE -> {
                    }
                    case ERROR -> {
                    }
                }

                logger.info("e********************************************************");

                return ChannelInterceptor.super.preSend(message, channel);
            }
        });
    }

    private void onConnected(StompHeaderAccessor accessor) {
        logger.info("onClientInboundConnected");
        String authHeader = accessor.getFirstNativeHeader("X-Authorization");

        logger.info("Auth Header: {}", authHeader);
    }
}
