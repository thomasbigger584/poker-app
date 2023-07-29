package com.twb.pokergame.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twb.pokergame.data.model.dto.pokertable.TableDTO;
import com.twb.pokergame.data.retrofit.api.PokerTableApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokerTableRepository extends BaseRepository {
    private static final String TAG = PokerTableRepository.class.getSimpleName();
    private final PokerTableApi api;
    private final MutableLiveData<List<TableDTO>> getPokerTablesLiveData = new MutableLiveData<>();

    public PokerTableRepository(PokerTableApi api) {
        this.api = api;
    }

    public LiveData<List<TableDTO>> getPokerTables() {
        Map<String, Integer> queryParams = new HashMap<>();
        api.getPokerTables(queryParams).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<TableDTO>> call, @NonNull Response<List<TableDTO>> response) {
                if (response.isSuccessful()) {
                    getPokerTablesLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue(new RuntimeException("Failed to get poker tables"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TableDTO>> call, @NonNull Throwable throwable) {
                errorLiveData.setValue(throwable);
            }
        });
        return getPokerTablesLiveData;
    }
}
