package com.twb.pokerapp.consumer;

import com.twb.pokerapp.configuration.ProfileConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(ProfileConfiguration.CLOUD_PROFILE)
public class KeycloakRabbitMqConsumer {

    @RabbitListener(queues = {"app.keycloak"})
    public void onAnyEvent(Message message) {
        try {
            log.debug("***************************************");
            log.debug("Props: {}", message.getMessageProperties());
            log.debug("Body: {}", new String(message.getBody()));
            log.debug("***************************************");
        } catch (Exception e) {
            log.error("Failed to print message", e);
        }
    }
}
