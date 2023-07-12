package com.twb.pokergame.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twb.pokergame.data.model.PokerTable;
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
    private final PokerTableApi api;
    private final MutableLiveData<List<PokerTable>> getPokerTablesLiveData = new MutableLiveData<>();

    public PokerTableRepository(PokerTableApi api) {
        this.api = api;
    }

    public LiveData<List<PokerTable>> getPokerTables() {
        Map<String, Integer> queryParams = new HashMap<>();
        api.getPokerTables(queryParams).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<PokerTable>> call, @NonNull Response<List<PokerTable>> response) {
                if (response.isSuccessful()) {
                    getPokerTablesLiveData.setValue(response.body());
                } else {
                    try (ResponseBody errorResponseBody = response.errorBody()) {
                        if (errorResponseBody != null) {
                            errorLiveData.setValue(new Exception(errorResponseBody.string()));
                        }
                    } catch (IOException e) {
                        errorLiveData.setValue(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PokerTable>> call, @NonNull Throwable throwable) {
                errorLiveData.setValue(throwable);
            }
        });
        return getPokerTablesLiveData;
    }
}
