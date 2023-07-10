package com.twb.pokergame.data.websocket.listener;

public interface RequestListener {
    void onSuccess();

    void onFailure(Throwable throwable);
}