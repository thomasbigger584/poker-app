package com.twb.stomplib.dto;

import org.jetbrains.annotations.NotNull;

public class StompHeader {
    public static final String VERSION = "accept-version";
    public static final String HEART_BEAT = "heart-beat";
    public static final String DESTINATION = "destination";
    public static final String SUBSCRIPTION = "subscription";
    public static final String CONTENT_TYPE = "content-type";
    public static final String MESSAGE_ID = "message-id";
    public static final String ID = "id";
    public static final String ACK = "ack";

    // Used by the Client to request a receipt
    public static final String RECEIPT = "receipt";

    // Used by the Server in the RECEIPT frame
    public static final String RECEIPT_ID = "receipt-id";

    private final String mKey;
    private final String mValue;

    public StompHeader(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    @NotNull
    @Override
    public String toString() {
        return "StompHeader{" + mKey + '=' + mValue + '}';
    }
}
