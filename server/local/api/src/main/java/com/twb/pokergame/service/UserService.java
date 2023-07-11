package com.twb.pokergame.service;

import com.twb.pokergame.domain.User;
import com.twb.pokergame.mapper.UserMapper;
import com.twb.pokergame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public User create(UserRepresentation representation) {
        User user = mapper.representationToModel(representation);
        return repository.save(user);
    }
}
