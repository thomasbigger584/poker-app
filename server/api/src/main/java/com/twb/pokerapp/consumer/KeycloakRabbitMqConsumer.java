package com.twb.pokerapp.consumer;

import com.twb.pokerapp.configuration.ProfileConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(ProfileConfiguration.DIGITALOCEAN_PROFILE)
public class KeycloakRabbitMqConsumer {

    @RabbitListener(queues = {"app.keycloak"})
    public void onAnyEvent(Message message) {
        try {
            log.info("***************************************");
            log.info("Props: {}", message.getMessageProperties());
            log.info("Body: {}", new String(message.getBody()));
            log.info("***************************************");
        } catch (Exception e) {
            log.error("Failed to print message", e);
        }
    }
}
