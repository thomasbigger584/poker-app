package com.twb.stomplib.event;

import java.util.Map;
import java.util.TreeMap;

public class LifecycleEvent {
    private final EventType type;
    //Nullable
    private Exception exception;
    //Nullable
    private String message;
    private Map<String, String> handshakeResponseHeaders = new TreeMap<>();

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

    public Map<String, String> getHandshakeResponseHeaders() {
        return this.handshakeResponseHeaders;
    }

    public void setHandshakeResponseHeaders(Map<String, String> handshakeResponseHeaders) {
        this.handshakeResponseHeaders = handshakeResponseHeaders;
    }

    public enum EventType {
        OPENED, CLOSED, ERROR
    }
}
