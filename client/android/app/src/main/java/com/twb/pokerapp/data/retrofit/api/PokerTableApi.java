package com.twb.pokerapp.data.retrofit.api;

import com.twb.pokerapp.data.model.dto.pokertable.TableDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface PokerTableApi {

    @GET("/poker-table")
    Call<List<TableDTO>> getPokerTables(
            @QueryMap(encoded = true) Map<String, Integer> params);
}
