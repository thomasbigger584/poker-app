package com.twb.pokerapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class BaseRepository {
    protected final MutableLiveData<Throwable> errorLiveData = new MutableLiveData<>();

    public LiveData<Throwable> getErrors() {
        return errorLiveData;
    }
}
