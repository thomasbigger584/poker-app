package com.twb.pokerapp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@RequiredArgsConstructor
public class TransactionConfiguration {
    @Getter
    private final TransactionTemplate writeTx;

    public TransactionTemplate getReadTx() {
        var transactionManager = writeTx.getTransactionManager();
        assert transactionManager != null;
        var template = new TransactionTemplate(transactionManager);
        template.setReadOnly(true);
        return template;
    }
}