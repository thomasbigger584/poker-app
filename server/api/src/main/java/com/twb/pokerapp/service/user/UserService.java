package com.twb.pokerapp.service.user;

import com.twb.pokerapp.configuration.Constants;
import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.PhysicalUser;
import com.twb.pokerapp.domain.enumeration.TransactionHistoryType;
import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.mapper.UserMapper;
import com.twb.pokerapp.proto.AppUserDTO;
import com.twb.pokerapp.proto.UserAmountDTO;
import com.twb.pokerapp.repository.UserRepository;
import com.twb.pokerapp.service.TransactionHistoryService;
import com.twb.pokerapp.web.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        var physicalUser = mapper.representationToModel(representation);
        return repository.save(physicalUser);
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
        return repository.findByUsername(principal.getName())
                .filter(PhysicalUser.class::isInstance)
                .map(PhysicalUser.class::cast)
                .map(user -> {
                    var resetFunds = Constants.INITIAL_USER_FUNDS;
                    var difference = resetFunds.subtract(user.getTotalFunds());
                    transactionHistoryService.create(user, difference, TransactionHistoryType.RESET);
                    user.setTotalFunds(resetFunds);
                    var savedUser = repository.save(user);
                    return mapper.modelToDto(savedUser);
                });
    }

    public Optional<AppUserDTO> deposit(Principal principal, UserAmountDTO amountDto) {
        if (principal == null) {
            return Optional.empty();
        }
        var amount = requirePositiveAmount(amountDto);
        return repository.findByUsername(principal.getName())
                .filter(PhysicalUser.class::isInstance)
                .map(PhysicalUser.class::cast)
                .map(user -> {
                    user.setTotalFunds(user.getTotalFunds().add(amount));
                    var savedUser = repository.save(user);
                    transactionHistoryService.create(savedUser, amount, TransactionHistoryType.DEPOSIT);
                    return mapper.modelToDto(savedUser);
                });
    }

    public Optional<AppUserDTO> withdraw(Principal principal, UserAmountDTO amountDto) {
        if (principal == null) {
            return Optional.empty();
        }
        var amount = requirePositiveAmount(amountDto);
        return repository.findByUsername(principal.getName())
                .filter(PhysicalUser.class::isInstance)
                .map(PhysicalUser.class::cast)
                .map(user -> {
                    if (user.getTotalFunds().compareTo(amount) < 0) {
                        throw new ValidationException("amount", "User does not have enough funds to withdraw " + amount);
                    }
                    user.setTotalFunds(user.getTotalFunds().subtract(amount));
                    var savedUser = repository.save(user);
                    transactionHistoryService.create(savedUser, amount.negate(), TransactionHistoryType.WITHDRAW);
                    return mapper.modelToDto(savedUser);
                });
    }

    /** Replaces the old {@code @NotNull @Positive} bean validation on the proto request body. */
    private BigDecimal requirePositiveAmount(UserAmountDTO amountDto) {
        var amount = ProtoConvert.bigDecimal(amountDto.getAmount());
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("amount", "Amount must be a positive number");
        }
        return amount;
    }
}
