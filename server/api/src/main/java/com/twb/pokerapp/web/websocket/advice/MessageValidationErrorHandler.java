package com.twb.pokerapp.web.websocket.advice;

import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationFieldDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.ArrayList;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class MessageValidationErrorHandler {
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;

    @MessageExceptionHandler
    public void handleValidationException(MethodArgumentNotValidException exception) {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("Cannot handle exception without authentication context", exception);
        }
        var username = authentication.getName();
        if (username == null) {
            throw new RuntimeException("Cannot handle exception without username", exception);
        }
        var validations = getValidations(exception);
        var message = messageFactory.validationErrors(validations);
        dispatcher.send(username, message);
    }

    private ValidationDTO getValidations(MethodArgumentNotValidException exception) {
        var bindingResult = exception.getBindingResult();
        var fieldErrors = new ArrayList<ValidationFieldDTO>();
        if (bindingResult != null) {
            for (var fieldError : bindingResult.getFieldErrors()) {
                var validationField = new ValidationFieldDTO();
                validationField.setField(fieldError.getField());
                validationField.setMessage(fieldError.getDefaultMessage());
                fieldErrors.add(validationField);
            }
        }
        var validationDto = new ValidationDTO();
        validationDto.setFields(fieldErrors);
        return validationDto;
    }
}
