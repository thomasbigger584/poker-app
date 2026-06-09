package com.twb.pokerapp.data.retrofit.api;

import com.twb.pokerapp.proto.AppUserDTO;
import com.twb.pokerapp.proto.AppUserListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AppUserApi {

    @GET("/api/app-user/current")
    Call<AppUserDTO> getCurrentUser();

    @GET("/api/app-user/bots")
    Call<AppUserListResponse> getBots();

    @POST("/api/app-user/reset-funds")
    Call<AppUserDTO> resetFunds();
}
