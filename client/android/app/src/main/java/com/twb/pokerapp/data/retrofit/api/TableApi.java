package com.twb.pokerapp.data.retrofit.api;

import com.twb.pokerapp.proto.AvailableTableListResponse;
import com.twb.pokerapp.proto.CreateTableDTO;
import com.twb.pokerapp.proto.TableDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface TableApi {

    @GET("/api/poker-table")
    Call<AvailableTableListResponse> getAvailableTables(
            @QueryMap(encoded = true) Map<String, Integer> params);

    @POST("/api/poker-table")
    Call<TableDTO> createTable(@Body CreateTableDTO createTableDto);
}
