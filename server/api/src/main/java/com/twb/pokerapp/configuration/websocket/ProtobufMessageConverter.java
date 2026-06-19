package com.twb.pokerapp.configuration.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * STOMP message converter for protobuf bodies. Outbound {@link com.google.protobuf.Message} payloads
 * are serialized to their binary form ({@code toByteArray}); inbound binary frames are parsed back
 * into the target proto type via its generated static {@code parseFrom(byte[])}. This is the binary
 * counterpart to {@code ProtobufHttpMessageConverter} on the REST side — Spring ships the latter but
 * has no messaging equivalent, so we register this one in the STOMP converter chain.
 *
 * <p>The default (and outbound) content type is {@code application/octet-stream} rather than
 * {@code application/x-protobuf}: Spring's {@code StompSubProtocolHandler} and
 * {@code WebSocketStompClient} only emit a <em>binary</em> WebSocket frame when the STOMP content type
 * is compatible with {@code application/octet-stream}. Any other type is sent as a text frame, which
 * UTF-8-mangles the binary protobuf body and breaks decoding ("Frame must be terminated with a null
 * octet"). All clients (the Spring test client and the Android stomp-lib) send
 * {@code application/octet-stream} too, so the wire uses a single binary content type; the legacy
 * {@code application/x-protobuf} is still accepted <em>inbound</em> as a defensive fallback for any
 * client that tags its binary body with the semantic protobuf type instead.
 */
public class ProtobufMessageConverter extends AbstractMessageConverter {
    public static final MimeType OCTET_STREAM = MimeTypeUtils.APPLICATION_OCTET_STREAM;
    public static final MimeType X_PROTOBUF = new MimeType("application", "x-protobuf");

    private final ConcurrentHashMap<Class<?>, Method> parserCache = new ConcurrentHashMap<>();

    public ProtobufMessageConverter() {
        // octet-stream first -> it is the default outbound content type used for binary WS framing.
        super(OCTET_STREAM, X_PROTOBUF);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return com.google.protobuf.Message.class.isAssignableFrom(clazz);
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        var payload = message.getPayload();
        byte[] bytes;
        if (payload instanceof byte[] raw) {
            bytes = raw;
        } else {
            throw new MessageConversionException("Expected byte[] protobuf payload but was " + payload.getClass());
        }
        try {
            var parseFrom = parserCache.computeIfAbsent(targetClass, ProtobufMessageConverter::parseFromMethod);
            return parseFrom.invoke(null, (Object) bytes);
        } catch (ReflectiveOperationException e) {
            throw new MessageConversionException("Failed to parse protobuf message of type " + targetClass, e);
        }
    }

    @Override
    protected Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
        return ((com.google.protobuf.Message) payload).toByteArray();
    }

    private static Method parseFromMethod(Class<?> targetClass) {
        try {
            return targetClass.getMethod("parseFrom", byte[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No parseFrom(byte[]) on protobuf type " + targetClass, e);
        }
    }
}
