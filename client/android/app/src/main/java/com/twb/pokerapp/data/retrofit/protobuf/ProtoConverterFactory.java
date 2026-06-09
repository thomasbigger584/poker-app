package com.twb.pokerapp.data.retrofit.protobuf;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Retrofit converter for protobuf-lite types (binary {@code application/x-protobuf}). Retrofit's
 * own {@code converter-protobuf} pulls in the full protobuf-java runtime; this hand-rolled factory
 * works against {@link MessageLite} so the app keeps the lean javalite runtime.
 */
public final class ProtoConverterFactory extends Converter.Factory {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/x-protobuf");

    public static ProtoConverterFactory create() {
        return new ProtoConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (!(type instanceof Class<?> cls) || !MessageLite.class.isAssignableFrom(cls)) {
            return null;
        }
        Parser<? extends MessageLite> parser = parserOf(cls);
        return new ResponseConverter<>(parser);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        if (!(type instanceof Class<?> cls) || !MessageLite.class.isAssignableFrom(cls)) {
            return null;
        }
        return new RequestConverter<>();
    }

    @SuppressWarnings("unchecked")
    private static Parser<? extends MessageLite> parserOf(Class<?> cls) {
        try {
            var defaultInstance = (MessageLite) cls.getMethod("getDefaultInstance").invoke(null);
            return (Parser<? extends MessageLite>) defaultInstance.getParserForType();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Not a protobuf message type: " + cls, e);
        }
    }

    private static final class RequestConverter<T extends MessageLite> implements Converter<T, RequestBody> {
        @Override
        public RequestBody convert(T value) {
            return RequestBody.create(MEDIA_TYPE, value.toByteArray());
        }
    }

    private static final class ResponseConverter<T extends MessageLite> implements Converter<ResponseBody, T> {
        private final Parser<T> parser;

        @SuppressWarnings("unchecked")
        ResponseConverter(Parser<? extends MessageLite> parser) {
            this.parser = (Parser<T>) parser;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            try (value) {
                return parser.parseFrom(value.bytes());
            }
        }
    }
}
