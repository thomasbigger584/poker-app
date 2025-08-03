package com.twb.pokerapp.consumer;

import com.twb.pokerapp.configuration.ProfileConfiguration;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile(ProfileConfiguration.DIGITALOCEAN_PROFILE)
public class KeycloakRabbitMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakRabbitMqConsumer.class);

    @RabbitListener(queues = {"app.keycloak"})
    public void onAnyEvent(Message message) {
        try {
            logger.info("***************************************");
            logger.info("Props: {}", message.getMessageProperties());
            logger.info("Body: {}", new String(message.getBody()));
            logger.info("***************************************");
        } catch (Exception e) {
            logger.error("Failed to print message", e);
        }
    }
}
