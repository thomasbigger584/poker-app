package com.twb.pokerapp.data.retrofit.api;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.HEAD;
import retrofit2.http.Url;

public interface HealthApi {
    @HEAD
    Single<Response<Void>> healthCheck(@Url String url);
}
