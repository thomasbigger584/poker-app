package com.twb.pokergame.data.websocket.message.server.payload;

public class LogMessageDTO {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogMessageDTO{" +
                "message='" + message + '\'' +
                '}';
    }
}
