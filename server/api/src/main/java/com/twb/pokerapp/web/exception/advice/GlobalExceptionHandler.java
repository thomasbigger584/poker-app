package com.twb.pokerapp.web.exception.advice;

import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.ApiErrorDTO;
import com.twb.pokerapp.proto.ValidationDTO;
import com.twb.pokerapp.proto.ValidationFieldDTO;
import com.twb.pokerapp.web.exception.NotFoundException;
import com.twb.pokerapp.web.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationDTO> handleValidationException(ValidationException ex) {
        var validationDTO = ValidationDTO.newBuilder()
                .addFields(ValidationFieldDTO.newBuilder()
                        .setField(ProtoConvert.text(ex.getField()))
                        .setMessage(ProtoConvert.text(ex.getMessage())))
                .build();
        return new ResponseEntity<>(validationDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var builder = ValidationDTO.newBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage();
            builder.addFields(ValidationFieldDTO.newBuilder()
                    .setField(ProtoConvert.text(fieldName))
                    .setMessage(ProtoConvert.text(errorMessage)));
        });
        return new ResponseEntity<>(builder.build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(apiError(HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(Exception ex) {
        return new ResponseEntity<>(apiError(HttpStatus.INTERNAL_SERVER_ERROR, ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiErrorDTO apiError(HttpStatus status, Exception ex) {
        return ApiErrorDTO.newBuilder()
                .setStatus(status.value())
                .setMessage(ProtoConvert.text(ex.getMessage()))
                .build();
    }
}
