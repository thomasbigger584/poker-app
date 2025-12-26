package com.twb.pokerapp.util;

import org.springframework.transaction.support.TransactionSynchronization;

import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;

public class TransactionUtil {

    public static void afterCommit(TransactionAfterCommitCallback callback) {
        var sync = new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                callback.onAfterCommit();
            }
        };
        registerSynchronization(sync);
    }

    @FunctionalInterface
    public interface TransactionAfterCommitCallback {
        void onAfterCommit();
    }
}
