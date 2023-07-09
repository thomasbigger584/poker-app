package com.twb.stomplib.stomp;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StompMessage {
    public static final String TERMINATE_MESSAGE_SYMBOL = "\u0000";
    private static final Pattern PATTERN_HEADER = Pattern.compile("([^:\\s]+)\\s*:\\s*([^:\\s]+)");

    private final String command;
    private final List<StompHeader> headers;
    private final String payload;

    public StompMessage(String command, String payload) {
        this(command, null, payload);
    }

    public StompMessage(String command, List<StompHeader> stompHeaders) {
        this(command, stompHeaders, null);
    }

    public StompMessage(String command, List<StompHeader> headers, String payload) {
        this.command = command;
        this.headers = headers;
        this.payload = payload;
    }

    public static StompMessage from(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            return new StompMessage(StompCommand.UNKNOWN, payload);
        }
        Scanner reader = new Scanner(new StringReader(payload));
        reader.useDelimiter("\\n");
        String command = reader.next();
        List<StompHeader> headers = new ArrayList<>();

        while (reader.hasNext(PATTERN_HEADER)) {
            Matcher matcher = PATTERN_HEADER.matcher(reader.next());
            matcher.find();
            headers.add(new StompHeader(matcher.group(1), matcher.group(2)));
        }

        reader.skip("\\s");

        reader.useDelimiter(TERMINATE_MESSAGE_SYMBOL);
        payload = reader.hasNext() ? reader.next() : null;

        return new StompMessage(command, headers, payload);
    }

    public List<StompHeader> getHeaders() {
        return headers;
    }

    public String getPayload() {
        return payload;
    }

    public String getCommand() {
        return command;
    }

    public String findHeader(String key) {
        if (headers == null) {
            return null;
        }
        for (StompHeader header : headers) {
            if (header.getKey().equals(key)) {
                return header.getValue();
            }
        }
        return null;
    }

    public String compile() {
        return compile(false);
    }

    public String compile(boolean legacyWhitespace) {
        StringBuilder builder = new StringBuilder();
        builder.append(command).append('\n');

        for (StompHeader header : headers) {
            builder.append(header.getKey())
                    .append(':')
                    .append(header.getValue())
                    .append('\n');
        }

        builder.append('\n');

        if (payload != null) {
            builder.append(payload);

            if (legacyWhitespace) {
                builder.append("\n\n");
            }
        }

        builder.append(TERMINATE_MESSAGE_SYMBOL);
        return builder.toString();
    }
}