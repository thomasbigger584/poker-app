package com.twb.pokerapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class BaseRepository {
    protected final MutableLiveData<Throwable> _errorLiveData = new MutableLiveData<>();
    public final LiveData<Throwable> errorLiveData = _errorLiveData;
}
