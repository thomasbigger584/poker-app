package com.twb.pokerapp.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokerapp.configuration.ProfileConfiguration;
import com.twb.pokerapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Profile(ProfileConfiguration.DIGITALOCEAN_PROFILE)
public class UserRabbitMqConsumer {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @RabbitListener(queues = {"app.keycloak.user.create"})
    public void onUserCreate(Message message) throws Exception {
        var event = objectMapper.readValue(message.getBody(), AdminEvent.class);
        var representation = toRepresentation(event);

        userService.create(representation);
    }

    @RabbitListener(queues = {"app.keycloak.user.register"})
    public void onUserRegister(Message message) throws Exception {
        var event = objectMapper.readValue(message.getBody(), Event.class);
        var representation = toRepresentation(event);

        userService.create(representation);
    }

    private UserRepresentation toRepresentation(AdminEvent adminEvent) throws Exception {
        var representation = objectMapper.readValue(adminEvent.getRepresentation(), UserRepresentation.class);
        representation.setId(adminEvent.getResourcePath().replace("users/", ""));
        return representation;
    }

    private UserRepresentation toRepresentation(Event event) {
        var representation = new UserRepresentation();
        representation.setId(event.getUserId());
        var details = event.getDetails();
        representation.setEmail(details.get("email"));
        representation.setUsername(details.get("username"));
        representation.setFirstName(details.get("first_name"));
        representation.setLastName(details.get("last_name"));
        return representation;
    }

}
