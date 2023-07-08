package com.twb.pokergame.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRabbitMqConsumer {
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "app.keycloak.user.create")
    public void onUserCreate(Message message) throws Exception {
        AdminEvent adminEvent = objectMapper.readValue(message.getBody(), AdminEvent.class);
        UserRepresentation user = objectMapper.readValue(adminEvent.getRepresentation(), UserRepresentation.class);
        System.out.println("user = " + user);
    }
}
