package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

public class ValidationFieldDTO {
    private String field;
    private String message;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return "ValidationFieldDTO{" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
