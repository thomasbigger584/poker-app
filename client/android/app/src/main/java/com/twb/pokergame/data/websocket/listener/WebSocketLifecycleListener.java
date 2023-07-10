package com.twb.pokergame.data.websocket.listener;

import com.twb.stomplib.event.LifecycleEvent;
import com.twb.stomplib.stomp.StompMessage;

public interface WebSocketLifecycleListener {
    void onOpened(LifecycleEvent lifecycleEvent);

    void onError(LifecycleEvent lifecycleEvent);

    void onClosed(LifecycleEvent lifecycleEvent);

    void onMessage(StompMessage stompMessage);
}