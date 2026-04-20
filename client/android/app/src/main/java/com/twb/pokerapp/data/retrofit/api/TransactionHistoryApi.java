package com.twb.pokerapp.data.retrofit.api;

import com.twb.pokerapp.data.model.dto.transaction.TransactionHistoryDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TransactionHistoryApi {

    @GET("/api/transaction-history/current")
    Call<List<TransactionHistoryDTO>> getCurrent(@Query("type") String type);
}
