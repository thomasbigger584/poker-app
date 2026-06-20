package com.twb.pokerapp.data.retrofit.api;

import com.twb.pokerapp.proto.TransactionHistoryListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TransactionHistoryApi {

    @GET("/api/transaction-history/current")
    Call<TransactionHistoryListResponse> getCurrent(@Query("type") String type);
}
