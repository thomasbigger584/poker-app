package com.twb.pokergame.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.twb.pokergame.data.model.PokerTable;
import com.twb.pokergame.data.retrofit.api.PokerTableApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            public void onResponse(Call<List<PokerTable>> call, Response<List<PokerTable>> response) {
                getPokerTablesLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PokerTable>> call, Throwable throwable) {
                errorLiveData.setValue(throwable);
            }
        });
        return getPokerTablesLiveData;
    }
}
