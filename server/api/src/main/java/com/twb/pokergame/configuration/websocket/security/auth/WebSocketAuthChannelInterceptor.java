package com.twb.pokergame.configuration.websocket.security.auth;

import com.twb.pokergame.configuration.jwt.JwtAuthConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);
    private static final String AUTHENTICATION_HEADER = "X-Authorization";

    private final JwtDecoder jwtDecoder;
    private final JwtAuthConverter jwtAuthConverter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            List<String> authorization = accessor.getNativeHeader(AUTHENTICATION_HEADER);
            String accessToken = authorization.get(0).split(" ")[1];
            Jwt jwt = jwtDecoder.decode(accessToken);

            AbstractAuthenticationToken authentication = jwtAuthConverter.convert(jwt);
            accessor.setUser(authentication);
        }
        return message;
    }
}