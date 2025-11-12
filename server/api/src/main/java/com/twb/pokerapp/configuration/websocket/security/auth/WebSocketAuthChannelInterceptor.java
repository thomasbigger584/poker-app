package com.twb.pokerapp.configuration.websocket.security.auth;

import com.twb.pokerapp.configuration.jwt.JwtAuthConverter;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final JwtAuthConverter jwtAuthConverter;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        var accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            log.warn("MessageHeaderAccessor is null");
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            var authorization = accessor.getNativeHeader(HttpHeaders.AUTHORIZATION);

            if (authorization == null) {
                log.warn("Header {} is null", HttpHeaders.AUTHORIZATION);
                return message;
            }

            var accessToken = authorization.getFirst().split(" ")[1];
            var jwt = jwtDecoder.decode(accessToken);

            var authentication = jwtAuthConverter.convert(jwt);
            accessor.setUser(authentication);
        }
        return message;
    }
}
