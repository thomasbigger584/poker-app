package com.twb.pokergame.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokergame.configuration.ProfileConfiguration;
import com.twb.pokergame.service.UserService;
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
        AdminEvent event = objectMapper.readValue(message.getBody(), AdminEvent.class);
        UserRepresentation representation = toRepresentation(event);

        userService.create(representation);
    }

    @RabbitListener(queues = {"app.keycloak.user.register"})
    public void onUserRegister(Message message) throws Exception {
        Event event = objectMapper.readValue(message.getBody(), Event.class);
        UserRepresentation representation = toRepresentation(event);

        userService.create(representation);
    }

    private UserRepresentation toRepresentation(AdminEvent adminEvent) throws Exception {
        UserRepresentation representation = objectMapper
                .readValue(adminEvent.getRepresentation(), UserRepresentation.class);
        representation.setId(adminEvent.getResourcePath()
                .replace("users/", ""));
        return representation;
    }

    private UserRepresentation toRepresentation(Event event) {
        UserRepresentation representation = new UserRepresentation();
        representation.setId(event.getUserId());
        Map<String, String> details = event.getDetails();
        representation.setEmail(details.get("email"));
        representation.setUsername(details.get("username"));
        representation.setFirstName(details.get("first_name"));
        representation.setLastName(details.get("last_name"));
        return representation;
    }

}
