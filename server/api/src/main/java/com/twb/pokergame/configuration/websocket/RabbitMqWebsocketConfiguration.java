package com.twb.pokergame.configuration.websocket;

import com.twb.pokergame.configuration.ProfileConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
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

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/poker-app-ws")
                .setAllowedOrigins(allowOrigins)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort)
                .setVirtualHost(virtualHost)
                .setClientLogin(clientLogin)
                .setClientPasscode(clientPasscode)
                .setSystemLogin(systemLogin)
                .setSystemPasscode(systemPasscode);
    }
}
