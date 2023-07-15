package com.twb.stomplib.pathmatcher;

import com.twb.stomplib.dto.StompHeader;
import com.twb.stomplib.dto.StompMessage;
import com.twb.stomplib.stomp.StompClient;

public class SubscriptionPathMatcher implements PathMatcher {

    private final StompClient stompClient;

    public SubscriptionPathMatcher(StompClient stompClient) {
        this.stompClient = stompClient;
    }

    @Override
    public boolean matches(String path, StompMessage msg) {
        // Compare subscription
        String pathSubscription = stompClient.getTopicId(path);
        if (pathSubscription == null) return false;
        String subscription = msg.findHeader(StompHeader.SUBSCRIPTION);
        return pathSubscription.equals(subscription);
    }
}