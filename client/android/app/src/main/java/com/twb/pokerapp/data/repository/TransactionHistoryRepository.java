package com.twb.pokerapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twb.pokerapp.data.model.dto.transaction.TransactionHistoryDTO;
import com.twb.pokerapp.data.retrofit.api.TransactionHistoryApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class TransactionHistoryRepository extends BaseRepository {
    private static final String TAG = TransactionHistoryRepository.class.getSimpleName();
    private final TransactionHistoryApi api;

    private final MutableLiveData<List<TransactionHistoryDTO>> _transactionsLiveData = new MutableLiveData<>();
    public final LiveData<List<TransactionHistoryDTO>> transactionsLiveData = _transactionsLiveData;

    @Inject
    public TransactionHistoryRepository(TransactionHistoryApi api) {
        this.api = api;
    }

    public void refreshCurrent(String type) {
        api.getCurrent(type).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<TransactionHistoryDTO>> call, @NonNull Response<List<TransactionHistoryDTO>> response) {
                if (response.isSuccessful()) {
                    _transactionsLiveData.setValue(response.body());
                } else {
                    _errorLiveData.setValue(new RuntimeException("Failed to get transaction histories: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TransactionHistoryDTO>> call, @NonNull Throwable throwable) {
                _errorLiveData.setValue(throwable);
            }
        });
    }
}
