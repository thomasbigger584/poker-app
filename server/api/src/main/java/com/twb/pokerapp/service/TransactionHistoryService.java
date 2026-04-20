package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.TransactionHistory;
import com.twb.pokerapp.domain.enumeration.TransactionHistoryType;
import com.twb.pokerapp.dto.transactionhistory.TransactionHistoryDTO;
import com.twb.pokerapp.mapper.TransactionHistoryMapper;
import com.twb.pokerapp.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {
    private final TransactionHistoryRepository repository;
    private final TransactionHistoryMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public TransactionHistory create(AppUser user, BigDecimal amount, TransactionHistoryType type) {
        var transaction = new TransactionHistory();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setType(type);
        return repository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionHistoryDTO> findCurrent(Principal principal, String typeStr, Pageable pageable) {
        TransactionHistoryType type = null;
        if (typeStr != null && !typeStr.equalsIgnoreCase("ALL")) {
            type = TransactionHistoryType.valueOf(typeStr.toUpperCase());
        }
        return repository.findByUsernameAndType(principal.getName(), type, pageable)
                .map(mapper::modelToDto);
    }
}
