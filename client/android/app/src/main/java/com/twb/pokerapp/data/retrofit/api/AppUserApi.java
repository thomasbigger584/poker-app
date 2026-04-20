package com.twb.pokerapp.data.retrofit.api;

import com.twb.pokerapp.data.model.dto.appuser.AppUserDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AppUserApi {

    @GET("/api/app-user/current")
    Call<AppUserDTO> getCurrentUser();

    @POST("/api/app-user/reset-funds")
    Call<Void> resetFunds();
}
