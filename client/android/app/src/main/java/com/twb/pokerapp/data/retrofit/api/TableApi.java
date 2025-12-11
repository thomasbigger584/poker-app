package com.twb.pokerapp.data.retrofit.api;

import com.twb.pokerapp.data.model.dto.table.CreateTableDTO;
import com.twb.pokerapp.data.model.dto.table.TableDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface TableApi {

    @GET("/api/poker-table")
    Call<List<TableDTO>> getTables(
            @QueryMap(encoded = true) Map<String, Integer> params);

    @POST("/api/poker-table")
    Call<TableDTO> createTable(@Body CreateTableDTO createTableDto);
}
