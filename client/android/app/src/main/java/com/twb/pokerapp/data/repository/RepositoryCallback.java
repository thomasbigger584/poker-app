package com.twb.pokerapp.data.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T result);

    void onFailure(Throwable t);
}
