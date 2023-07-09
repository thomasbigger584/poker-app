package com.twb.pokergame.rest;

import io.reactivex.Completable;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceRepository {

    @POST("hello-convert-and-send")
    Completable sendRestEcho(@Query("msg") String message);
}
