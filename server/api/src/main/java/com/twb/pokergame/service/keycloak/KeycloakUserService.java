package com.twb.pokergame.service.keycloak;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakUserService {
    private final UsersResource usersResource;

    @PostConstruct
    public void init() {
        List<UserRepresentation> list = usersResource.list();

        for (UserRepresentation representation : list) {
            System.out.println("representation = " + ReflectionToStringBuilder.toString(representation));
        }
    }
}
