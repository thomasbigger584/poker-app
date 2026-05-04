package com.twb.pokerapp.data.model.dto.transactionhistory;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionHistoryDTO {
    private UUID id;
    private Double amount;
    private String type;
    private LocalDateTime createdDateTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
