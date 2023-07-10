package com.twb.stomplib.event;

import java.util.Map;
import java.util.TreeMap;

public class LifecycleEvent {
    private final EventType type;
    //Nullable
    private Exception exception;
    //Nullable
    private String message;
    private Map<String, String> responseHeaders = new TreeMap<>();

    public LifecycleEvent(EventType type) {
        this.type = type;
    }

    public LifecycleEvent(EventType type, Exception exception) {
        this.type = type;
        this.exception = exception;
    }

    public LifecycleEvent(EventType type, String message) {
        this.type = type;
        this.message = message;
    }

    public EventType getType() {
        return this.type;
    }

    public Exception getException() {
        return exception;
    }

    public String getMessage() {
        return this.message;
    }

    public Map<String, String> getResponseHeaders() {
        return this.responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public enum EventType {
        OPENED, CLOSED, ERROR
    }

    @Override
    public String toString() {
        return "LifecycleEvent{" +
                "type=" + type +
                ", exception=" + exception +
                ", message='" + message + '\'' +
                ", responseHeaders=" + responseHeaders +
                '}';
    }
}
