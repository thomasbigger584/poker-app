package com.twb.pokerapp.testutils.http.message;

import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * STOMP {@link MessageConverter} for the test client, mirroring the server's binary-protobuf wire
 * format. Inbound frames carry a raw protobuf body which is parsed into the target proto type via
 * its generated static {@code parseFrom(byte[])}; outbound proto payloads are serialized with
 * {@code toByteArray()}. This is the test-side counterpart to the server's
 * {@code ProtobufMessageConverter}.
 */
@Slf4j
public class ServerMessageConverter implements MessageConverter {
    private static final ConcurrentHashMap<Class<?>, Method> PARSER_CACHE = new ConcurrentHashMap<>();

    @Override
    public Object fromMessage(org.springframework.messaging.Message<?> message, @NotNull Class<?> targetClass) {
        var payload = (byte[]) message.getPayload();
        try {
            var parseFrom = PARSER_CACHE.computeIfAbsent(targetClass, ServerMessageConverter::parseFromMethod);
            return parseFrom.invoke(null, (Object) payload);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to parse protobuf message of type " + targetClass, e);
        }
    }

    @Override
    public org.springframework.messaging.Message<?> toMessage(@NotNull Object payload, MessageHeaders headers) {
        var bytes = ((Message) payload).toByteArray();
        return new GenericMessage<>(bytes, headers);
    }

    private static Method parseFromMethod(Class<?> targetClass) {
        try {
            return targetClass.getMethod("parseFrom", byte[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No parseFrom(byte[]) on protobuf type " + targetClass, e);
        }
    }
}
