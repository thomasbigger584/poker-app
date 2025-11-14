package com.twb.pokerapp.service.keycloak;

import com.twb.pokerapp.mapper.UserMapper;
import com.twb.pokerapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.keycloak.admin.client.resource.GroupResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Component
public class KeycloakUserService {

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
        log.info("Synchronizing Keycloak users...");
        var userMembers = userGroupResource.members();
        var databaseUsersFetched = new ArrayList<>(userRepository.findAll());

        var updatedUsers = 0;
        var createdUsers = 0;

        for (var representation : userMembers) {
            log.info(ReflectionToStringBuilder.toString(representation, ToStringStyle.JSON_STYLE));

            var id = UUID.fromString(representation.getId());
            var userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                var appUser = userOpt.get();
                log.info("User {} already exists in database - todo: consider updating user here", appUser.getId());
                databaseUsersFetched.remove(appUser);
                updatedUsers++;
            } else {
                var appUser = userMapper.representationToModel(representation);
                userRepository.save(appUser);
                createdUsers++;
            }
        }
        userRepository.deleteAll(databaseUsersFetched);
        log.info("Keycloak user sync completed. " +
                "Updated: {}, Created: {}, Deleted: {}", updatedUsers, createdUsers, databaseUsersFetched.size());
    }
}
