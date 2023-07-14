package com.twb.pokergame.data.websocket.message.client;

public class SendChatMessageDTO {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SendChatMessageDTO{" +
                "message='" + message + '\'' +
                '}';
    }
}
