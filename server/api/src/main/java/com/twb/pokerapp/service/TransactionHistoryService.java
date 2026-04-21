package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.TransactionHistory;
import com.twb.pokerapp.domain.enumeration.TransactionHistoryType;
import com.twb.pokerapp.dto.transactionhistory.TransactionHistoryDTO;
import com.twb.pokerapp.mapper.TransactionHistoryMapper;
import com.twb.pokerapp.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {
    private final TransactionHistoryRepository repository;
    private final TransactionHistoryMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<TransactionHistory> create(AppUser user, BigDecimal amount, TransactionHistoryType type) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return Optional.empty();
        }
        var transaction = new TransactionHistory();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setType(type);

        transaction = repository.save(transaction);

        return Optional.of(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionHistoryDTO> findCurrent(Principal principal, String typeStr, Pageable pageable) {
        if (typeStr.equals("ALL_SIMPLIFIED")) {
            throw new NotImplementedException("Not Implemented yet");
        }
        return repository.findByUsername(principal.getName(), pageable)
                .map(mapper::modelToDto);
    }
}
