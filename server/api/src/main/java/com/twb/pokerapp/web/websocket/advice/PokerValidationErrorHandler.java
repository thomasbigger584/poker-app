package com.twb.pokerapp.web.websocket.advice;

import com.twb.pokerapp.web.websocket.message.MessageDispatcher;
import com.twb.pokerapp.web.websocket.message.server.ServerMessageFactory;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationDTO;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationFieldDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.ArrayList;

@ControllerAdvice
@RequiredArgsConstructor
public class PokerValidationErrorHandler {
    private final ServerMessageFactory messageFactory;
    private final MessageDispatcher dispatcher;

    @MessageExceptionHandler
    public void handleValidationException(MethodArgumentNotValidException exception, SimpMessageHeaderAccessor headerAccessor) {
        var validations = getValidations(exception);

        var username = getSessionId(headerAccessor);
        var message = messageFactory.validationErrors(validations);

        dispatcher.send(username, message);
    }

    private ValidationDTO getValidations(MethodArgumentNotValidException exception) {
        var bindingResult = exception.getBindingResult();
        var fieldErrors = new ArrayList<ValidationFieldDTO>();
        for (var fieldError : bindingResult.getFieldErrors()) {
            var validationField = new ValidationFieldDTO();
            validationField.setField(fieldError.getField());
            validationField.setMessage(fieldError.getDefaultMessage());
            fieldErrors.add(validationField);
        }
        var validationDto = new ValidationDTO();
        validationDto.setFields(fieldErrors);
        return validationDto;
    }

    private String getSessionId(SimpMessageHeaderAccessor headerAccessor) {
        var user = headerAccessor.getUser();
        if (user != null) {
            return user.getName();
        }
        return headerAccessor.getSessionId();
    }
}