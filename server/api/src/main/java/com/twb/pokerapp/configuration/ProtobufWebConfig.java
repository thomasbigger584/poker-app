package com.twb.pokerapp.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Registers a single protobuf HTTP converter that serves both wire formats from the same generated
 * types: {@code application/x-protobuf} (binary) and {@code application/json} (protobuf canonical
 * JSON). The format is chosen per request by the client's {@code Accept} header — one code path,
 * no parallel DTO/serializer stack. Non-proto bodies fall through to the default Jackson converter.
 */
@Configuration
public class ProtobufWebConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new ProtobufJsonFormatHttpMessageConverter());
    }
}
