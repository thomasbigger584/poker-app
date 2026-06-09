package com.twb.pokerapp.web.exception.advice;

import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.ValidationDTO;
import com.twb.pokerapp.proto.ValidationFieldDTO;
import com.twb.pokerapp.web.exception.ValidationException;
import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class MessageValidationErrorHandler {
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;

    @MessageExceptionHandler
    public void handleValidationException(MethodArgumentNotValidException exception, StompHeaderAccessor headerAccessor) {
        var username = requireUsername(exception);
        dispatcher.sendReceipt(headerAccessor);
        dispatcher.send(username, messageFactory.validationErrors(getValidations(exception)));
    }

    @MessageExceptionHandler
    public void handleManualValidationException(ValidationException exception, StompHeaderAccessor headerAccessor) {
        var username = requireUsername(exception);
        var validations = ValidationDTO.newBuilder()
                .addFields(ValidationFieldDTO.newBuilder()
                        .setField(ProtoConvert.text(exception.getField()))
                        .setMessage(ProtoConvert.text(exception.getMessage())))
                .build();
        dispatcher.sendReceipt(headerAccessor);
        dispatcher.send(username, messageFactory.validationErrors(validations));
    }

    private String requireUsername(Exception exception) {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("Cannot handle exception without authentication context", exception);
        }
        var username = authentication.getName();
        if (username == null) {
            throw new RuntimeException("Cannot handle exception without username", exception);
        }
        return username;
    }

    private ValidationDTO getValidations(MethodArgumentNotValidException exception) {
        var builder = ValidationDTO.newBuilder();
        var bindingResult = exception.getBindingResult();
        if (bindingResult != null) {
            for (var fieldError : bindingResult.getFieldErrors()) {
                builder.addFields(ValidationFieldDTO.newBuilder()
                        .setField(ProtoConvert.text(fieldError.getField()))
                        .setMessage(ProtoConvert.text(fieldError.getDefaultMessage())));
            }
        }
        return builder.build();
    }
}
