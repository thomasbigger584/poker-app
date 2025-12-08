package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import java.util.List;

public class ValidationDTO {
    private List<ValidationFieldDTO> fields;

    public List<ValidationFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<ValidationFieldDTO> fields) {
        this.fields = fields;
    }

    @NonNull
    @Override
    public String toString() {
        return "ValidationDTO{" +
                "fields=" + fields +
                '}';
    }
}
