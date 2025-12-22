package com.twb.pokerapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class TransactionConfiguration {

    @Bean
    @Primary
    public TransactionTemplate writeTx(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean("readTx")
    public TransactionTemplate readTx(PlatformTransactionManager transactionManager) {
        var template = new TransactionTemplate(transactionManager);
        template.setReadOnly(true);
        return template;
    }
}