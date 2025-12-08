package com.twb.pokerapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.data.retrofit.api.PokerTableApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableRepository extends BaseRepository {
    private static final String TAG = TableRepository.class.getSimpleName();
    private final PokerTableApi api;
    private final MutableLiveData<List<TableDTO>> getTablesLiveData = new MutableLiveData<>();

    public TableRepository(PokerTableApi api) {
        this.api = api;
    }

    public LiveData<List<TableDTO>> getTables() {
        Map<String, Integer> queryParams = new HashMap<>();
        api.getTables(queryParams).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<TableDTO>> call, @NonNull Response<List<TableDTO>> response) {
                if (response.isSuccessful()) {
                    getTablesLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue(new RuntimeException("Failed to get tables"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TableDTO>> call, @NonNull Throwable throwable) {
                errorLiveData.setValue(throwable);
            }
        });
        return getTablesLiveData;
    }
}
