package com.twb.pokerapp.configuration.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.MimeType;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * STOMP message converter for protobuf bodies. Outbound {@link com.google.protobuf.Message} payloads
 * are serialized to their binary form ({@code toByteArray}); inbound binary frames are parsed back
 * into the target proto type via its generated static {@code parseFrom(byte[])}. This is the binary
 * counterpart to {@code ProtobufHttpMessageConverter} on the REST side — Spring ships the latter but
 * has no messaging equivalent, so we register this one in the STOMP converter chain.
 */
public class ProtobufMessageConverter extends AbstractMessageConverter {
    public static final MimeType PROTOBUF = new MimeType("application", "x-protobuf");

    private final ConcurrentHashMap<Class<?>, Method> parserCache = new ConcurrentHashMap<>();

    public ProtobufMessageConverter() {
        super(PROTOBUF);
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
