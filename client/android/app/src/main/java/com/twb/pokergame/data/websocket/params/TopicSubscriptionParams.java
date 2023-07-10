package com.twb.pokergame.data.websocket.params;

import com.twb.pokergame.data.websocket.listener.WebSocketLifecycleListener;

public class TopicSubscriptionParams {
    private String topic;
    private WebSocketLifecycleListener listener;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public WebSocketLifecycleListener getListener() {
        return listener;
    }

    public void setListener(WebSocketLifecycleListener listener) {
        this.listener = listener;
    }
}
