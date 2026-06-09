package com.twb.pokerapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twb.pokerapp.proto.AvailableTableDTO;
import com.twb.pokerapp.proto.AvailableTableListResponse;
import com.twb.pokerapp.proto.CreateTableDTO;
import com.twb.pokerapp.proto.TableDTO;
import com.twb.pokerapp.data.retrofit.api.TableApi;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableRepository extends BaseRepository {
    private static final String TAG = TableRepository.class.getSimpleName();
    private final TableApi api;
    private final MutableLiveData<List<AvailableTableDTO>> _tablesLiveData = new MutableLiveData<>();
    public final LiveData<List<AvailableTableDTO>> tablesLiveData = _tablesLiveData;

    private final MutableLiveData<TableDTO> _createdTableLiveData = new MutableLiveData<>();
    public final LiveData<TableDTO> createTableLiveData = _createdTableLiveData;

    public TableRepository(TableApi api) {
        this.api = api;
    }

    public void refreshAvailableTables() {
        api.getAvailableTables(new HashMap<>()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AvailableTableListResponse> call, @NonNull Response<AvailableTableListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _tablesLiveData.setValue(response.body().getTablesList());
                } else {
                    _errorLiveData.setValue(new RuntimeException("Failed to get tables: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AvailableTableListResponse> call, @NonNull Throwable throwable) {
                _errorLiveData.setValue(throwable);
            }
        });
    }

    public void createTable(CreateTableDTO dto) {
        api.createTable(dto).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TableDTO> call, @NonNull Response<TableDTO> response) {
                if (response.isSuccessful()) {
                    _createdTableLiveData.setValue(response.body());
                } else {
                    _errorLiveData.setValue(new RuntimeException("Failed to create table: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<TableDTO> call, @NonNull Throwable throwable) {
                _errorLiveData.setValue(throwable);
            }
        });
    }
}

