package com.twb.pokerapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twb.pokerapp.data.model.dto.table.AvailableTableDTO;
import com.twb.pokerapp.data.model.dto.table.CreateTableDTO;
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.data.retrofit.api.TableApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableRepository extends BaseRepository {
    private static final String TAG = TableRepository.class.getSimpleName();
    private final TableApi api;
    private final MutableLiveData<List<AvailableTableDTO>> getTablesLiveData = new MutableLiveData<>();
    private final MutableLiveData<TableDTO> createdTableLiveData = new MutableLiveData<>();

    public TableRepository(TableApi api) {
        this.api = api;
    }

    public LiveData<List<AvailableTableDTO>> getAvailableTables() {
        var queryParams = new HashMap<String, Integer>();
        api.getAvailableTables(queryParams).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<AvailableTableDTO>> call, @NonNull Response<List<AvailableTableDTO>> response) {
                if (response.isSuccessful()) {
                    getTablesLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue(new RuntimeException("Failed to get tables"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AvailableTableDTO>> call, @NonNull Throwable throwable) {
                errorLiveData.setValue(throwable);
            }
        });
        return getTablesLiveData;
    }

    public void createTable(CreateTableDTO dto) {
        api.createTable(dto).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TableDTO> call, @NonNull Response<TableDTO> response) {
                if (response.isSuccessful()) {
                    createdTableLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue(new RuntimeException("Failed to create table"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<TableDTO> call, @NonNull Throwable throwable) {
                errorLiveData.setValue(throwable);
            }
        });
    }

    public MutableLiveData<TableDTO> getCreatedTableLiveData() {
        return createdTableLiveData;
    }
}

