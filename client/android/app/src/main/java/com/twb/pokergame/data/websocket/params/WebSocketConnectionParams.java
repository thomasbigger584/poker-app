package com.twb.pokergame.data.websocket.params;

public class WebSocketConnectionParams {
    private String endpoint;
    private String token;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "WebSocketConnectionParams{" +
                "endpoint='" + endpoint + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
