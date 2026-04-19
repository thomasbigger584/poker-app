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
            public void onResponse(@NonNull Call<List<AvailableTableDTO>> call, @NonNull Response<List<AvailableTableDTO>> response) {
                if (response.isSuccessful()) {
                    _tablesLiveData.setValue(response.body());
                } else {
                    _errorLiveData.setValue(new RuntimeException("Failed to get tables: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AvailableTableDTO>> call, @NonNull Throwable throwable) {
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

