package com.twb.stomplib.dto;

import org.jetbrains.annotations.NotNull;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * A STOMP frame. The command and headers are always UTF-8 text, but the body may be binary
 * (e.g. protobuf), so it is held as raw {@code byte[]}. On the wire the body is delimited by the
 * {@code content-length} header when present (which makes embedded NUL/newline bytes safe) and
 * falls back to the NUL terminator for text frames.
 */
public class StompMessage {

    private static final byte NULL_BYTE = 0;
    private static final byte LF = '\n';

    private static final Pattern PATTERN_HEADER = Pattern.compile("([^:\\s]+)\\s*:\\s*([^:\\s]+)");

    private final String mStompCommand;
    private final List<StompHeader> mStompHeaders;
    private final byte[] mPayload;

    public StompMessage(String stompCommand, List<StompHeader> stompHeaders, byte[] payload) {
        mStompCommand = stompCommand;
        mStompHeaders = stompHeaders;
        mPayload = payload;
    }

    public static StompMessage from(@Nullable byte[] data) {
        if (data == null || isBlank(data)) {
            // Heartbeat / empty frame — keep the raw bytes so getPayload() still yields e.g. "\n".
            return new StompMessage(StompCommand.UNKNOWN, null, data);
        }

        var separator = indexOfHeaderEnd(data);
        var headerSection = new String(data, 0, separator, StandardCharsets.UTF_8);

        var reader = new Scanner(new StringReader(headerSection));
        reader.useDelimiter("\\n");
        var command = reader.next();
        List<StompHeader> headers = new ArrayList<>();
        while (reader.hasNext(PATTERN_HEADER)) {
            var matcher = PATTERN_HEADER.matcher(reader.next());
            matcher.find();
            headers.add(new StompHeader(matcher.group(1), matcher.group(2)));
        }

        var bodyStart = Math.min(separator + 2, data.length); // skip the blank line ("\n\n")
        var contentLength = contentLength(headers);
        int bodyEnd;
        if (contentLength >= 0) {
            bodyEnd = Math.min(bodyStart + contentLength, data.length);
        } else {
            bodyEnd = indexOf(data, NULL_BYTE, bodyStart);
            if (bodyEnd < 0) bodyEnd = data.length;
        }
        var payload = Arrays.copyOfRange(data, bodyStart, Math.max(bodyStart, bodyEnd));
        return new StompMessage(command, headers, payload);
    }

    public List<StompHeader> getStompHeaders() {
        return mStompHeaders;
    }

    /** Raw body bytes (binary-safe), e.g. for protobuf {@code parseFrom}. */
    @Nullable
    public byte[] getPayloadBytes() {
        return mPayload;
    }

    /** Body decoded as UTF-8 text. Use {@link #getPayloadBytes()} for binary payloads. */
    @Nullable
    public String getPayload() {
        return mPayload == null ? null : new String(mPayload, StandardCharsets.UTF_8);
    }

    public String getStompCommand() {
        return mStompCommand;
    }

    @Nullable
    public String findHeader(String key) {
        if (mStompHeaders == null) return null;
        for (var header : mStompHeaders) {
            if (header.getKey().equals(key)) return header.getValue();
        }
        return null;
    }

    @NonNull
    public byte[] compile() {
        return compile(false);
    }

    @NonNull
    public byte[] compile(boolean legacyWhitespace) {
        var headerBuilder = new StringBuilder();
        headerBuilder.append(mStompCommand).append('\n');
        if (mStompHeaders != null) {
            for (var header : mStompHeaders) {
                headerBuilder.append(header.getKey()).append(':').append(header.getValue()).append('\n');
            }
        }
        headerBuilder.append('\n');
        var headerBytes = headerBuilder.toString().getBytes(StandardCharsets.UTF_8);
        var body = mPayload != null ? mPayload : new byte[0];
        var trailer = legacyWhitespace ? 2 : 0;
        var out = new byte[headerBytes.length + body.length + trailer + 1];
        System.arraycopy(headerBytes, 0, out, 0, headerBytes.length);
        System.arraycopy(body, 0, out, headerBytes.length, body.length);
        var pos = headerBytes.length + body.length;
        if (legacyWhitespace) {
            out[pos++] = LF;
            out[pos++] = LF;
        }
        out[pos] = NULL_BYTE;
        return out;
    }

    @NotNull
    @Override
    public String toString() {
        return "StompMessage{" +
                "command='" + mStompCommand + '\'' +
                ", headers=" + mStompHeaders +
                ", payloadBytes=" + (mPayload == null ? 0 : mPayload.length) +
                '}';
    }

    // ----- helpers -----

    private static boolean isBlank(byte[] data) {
        for (var b : data) {
            if (b != ' ' && b != '\t' && b != '\r' && b != '\n') {
                return false;
            }
        }
        return true;
    }

    /** Index of the first blank line ("\n\n") separating headers from the body. */
    private static int indexOfHeaderEnd(byte[] data) {
        for (var i = 0; i < data.length - 1; i++) {
            if (data[i] == LF && data[i + 1] == LF) {
                return i;
            }
        }
        return data.length;
    }

    private static int indexOf(byte[] data, byte target, int from) {
        for (var i = from; i < data.length; i++) {
            if (data[i] == target) return i;
        }
        return -1;
    }

    private static int contentLength(List<StompHeader> headers) {
        for (var header : headers) {
            if (StompHeader.CONTENT_LENGTH.equals(header.getKey())) {
                try {
                    return Integer.parseInt(header.getValue().trim());
                } catch (NumberFormatException ignored) {
                    return -1;
                }
            }
        }
        return -1;
    }
}
