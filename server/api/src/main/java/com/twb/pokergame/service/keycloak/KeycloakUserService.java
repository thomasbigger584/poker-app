package com.twb.pokergame.service.keycloak;

import com.twb.pokergame.domain.User;
import com.twb.pokergame.mapper.UserMapper;
import com.twb.pokergame.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class KeycloakUserService {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserService.class);

    @Autowired
    @Qualifier("userGroupResource")
    private GroupResource userGroupResource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    /*
     * Synchronizing users api database with those stored in keycloak on application startup
     */
    @PostConstruct
    public void init() {
        List<UserRepresentation> userMembers = userGroupResource.members();
        List<User> databaseUsersFetched = new ArrayList<>(userRepository.findAll());

        for (UserRepresentation representation : userMembers) {
            logger.info("Keycloak Users: " + ReflectionToStringBuilder.toString(representation, ToStringStyle.JSON_STYLE));

            UUID id = UUID.fromString(representation.getId());
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                logger.info("User {} already exists in database - todo: consider updating user here", user.getId());
                databaseUsersFetched.remove(user);
            } else {
                User user = userMapper.representationToModel(representation);
                userRepository.save(user);
            }
        }
        userRepository.deleteAll(databaseUsersFetched);
    }
}
