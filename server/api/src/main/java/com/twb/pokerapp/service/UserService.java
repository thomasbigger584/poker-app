package com.twb.pokerapp.service;

import com.twb.pokerapp.configuration.Constants;
import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.enumeration.TransactionHistoryType;
import com.twb.pokerapp.dto.appuser.AppUserDTO;
import com.twb.pokerapp.mapper.UserMapper;
import com.twb.pokerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final TransactionHistoryService transactionHistoryService;

    public AppUser create(UserRepresentation representation) {
        var appUser = mapper.representationToModel(representation);
        return repository.save(appUser);
    }

    @Transactional(readOnly = true)
    public Optional<AppUserDTO> getCurrentUser(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        return repository.findByUsername(principal.getName())
                .map(mapper::modelToDto);
    }

    public Optional<AppUserDTO> resetFunds(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        return repository.findByUsername(principal.getName()).map(user -> {
            var resetFunds = Constants.INITIAL_USER_FUNDS;
            user.setTotalFunds(resetFunds);
            user = repository.save(user);
            transactionHistoryService.create(user, resetFunds, TransactionHistoryType.CREDIT);
            return mapper.modelToDto(user);
        });
    }
}
