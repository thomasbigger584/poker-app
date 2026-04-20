package com.twb.pokerapp.dto.transactionhistory;

import com.twb.pokerapp.domain.enumeration.TransactionHistoryType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionHistoryDTO {
    private UUID id;
    private BigDecimal amount;
    private TransactionHistoryType type;
    private LocalDateTime createdDateTime;
}
