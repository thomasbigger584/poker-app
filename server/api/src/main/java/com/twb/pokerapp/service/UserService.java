package com.twb.pokerapp.service;

import com.twb.pokerapp.configuration.Constants;
import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.enumeration.TransactionHistoryType;
import com.twb.pokerapp.dto.appuser.AppUserDTO;
import com.twb.pokerapp.dto.appuser.UserAmountDTO;
import com.twb.pokerapp.exception.ValidationException;
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
            var difference = resetFunds.subtract(user.getTotalFunds());
            transactionHistoryService.create(user, difference, TransactionHistoryType.RESET);
            return mapper.modelToDto(user);
        });
    }

    public Optional<AppUserDTO> deposit(Principal principal, UserAmountDTO amountDto) {
        if (principal == null) {
            return Optional.empty();
        }
        return repository.findByUsername(principal.getName()).map(user -> {
            user.setTotalFunds(user.getTotalFunds().add(amountDto.getAmount()));
            user = repository.save(user);
            transactionHistoryService.create(user, amountDto.getAmount(), TransactionHistoryType.DEPOSIT);
            return mapper.modelToDto(user);
        });
    }

    public Optional<AppUserDTO> withdraw(Principal principal, UserAmountDTO amountDto) {
        if (principal == null) {
            return Optional.empty();
        }
        return repository.findByUsername(principal.getName()).map(user -> {
            if (user.getTotalFunds().compareTo(amountDto.getAmount()) < 0) {
                throw new ValidationException("User does not have enough funds to withdraw " + amountDto.getAmount());
            }
            user.setTotalFunds(user.getTotalFunds().subtract(amountDto.getAmount()));
            user = repository.save(user);
            transactionHistoryService.create(user, amountDto.getAmount(), TransactionHistoryType.WITHDRAW);
            return mapper.modelToDto(user);
        });
    }
}
