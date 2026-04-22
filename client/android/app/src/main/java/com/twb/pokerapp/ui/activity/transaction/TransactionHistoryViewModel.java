package com.twb.pokerapp.ui.activity.transaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.data.model.dto.transactionhistory.TransactionHistoryDTO;
import com.twb.pokerapp.data.repository.TransactionHistoryRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TransactionHistoryViewModel extends ViewModel {
    private final TransactionHistoryRepository repository;
    public final LiveData<List<TransactionHistoryDTO>> transactionsLiveData;
    public final LiveData<Throwable> errorLiveData;

    @Inject
    public TransactionHistoryViewModel(TransactionHistoryRepository repository) {
        this.repository = repository;
        this.transactionsLiveData = repository.transactionsLiveData;
        this.errorLiveData = repository.errorLiveData;
    }

    public void refresh(String type) {
        repository.refreshCurrent(type);
    }
}
