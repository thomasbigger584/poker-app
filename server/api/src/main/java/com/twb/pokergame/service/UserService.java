package com.twb.pokergame.service;

import com.twb.pokergame.domain.User;
import com.twb.pokergame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User create(UserRepresentation representation) {

        //todo: mapstruct
        User user = new User();
        user.setId(UUID.fromString(representation.getId()));
        user.setUsername(representation.getUsername());
        user.setFirstName(representation.getFirstName());
        user.setLastName(representation.getLastName());
        user.setEmail(representation.getEmail());
        user.setEmailVerified(representation.isEmailVerified());
        user.setEnabled(representation.isEnabled());
        List<String> groups = representation.getGroups();
        if (groups != null) {
            user.setGroups(groups.stream()
                    .map(group -> group.replace("/", ""))
                    .toList());
        }
        return repository.save(user);
    }
}
