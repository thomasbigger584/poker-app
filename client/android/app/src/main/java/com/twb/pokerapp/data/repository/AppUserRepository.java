package com.twb.pokerapp.data.repository;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.appuser.AppUserDTO;
import com.twb.pokerapp.data.model.dto.appuser.BotDTO;
import com.twb.pokerapp.data.retrofit.api.AppUserApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppUserRepository extends BaseRepository {
    private final AppUserApi api;

    public AppUserRepository(AppUserApi api) {
        this.api = api;
    }

    public void getCurrentUser(RepositoryCallback<AppUserDTO> callback) {
        api.getCurrentUser().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AppUserDTO> call, @NonNull Response<AppUserDTO> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    var exception = new RuntimeException("Failed to get current user: " + response.code());
                    _errorLiveData.setValue(exception);
                    callback.onFailure(exception);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppUserDTO> call, @NonNull Throwable throwable) {
                _errorLiveData.setValue(throwable);
                callback.onFailure(throwable);
            }
        });
    }

    public void getBots(RepositoryCallback<List<BotDTO>> callback) {
        api.getBots().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<BotDTO>> call, @NonNull Response<List<BotDTO>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    var exception = new RuntimeException("Failed to get bots: " + response.code());
                    _errorLiveData.setValue(exception);
                    callback.onFailure(exception);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BotDTO>> call, @NonNull Throwable throwable) {
                _errorLiveData.setValue(throwable);
                callback.onFailure(throwable);
            }
        });
    }

    public void resetFunds(RepositoryCallback<AppUserDTO> callback) {
        api.resetFunds().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AppUserDTO> call, @NonNull Response<AppUserDTO> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    var exception = new RuntimeException("Failed to reset funds: " + response.code());
                    _errorLiveData.setValue(exception);
                    callback.onFailure(exception);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppUserDTO> call, @NonNull Throwable throwable) {
                _errorLiveData.setValue(throwable);
                callback.onFailure(throwable);
            }
        });
    }
}
