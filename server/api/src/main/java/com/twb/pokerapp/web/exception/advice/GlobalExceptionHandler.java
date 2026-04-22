package com.twb.pokerapp.web.exception.advice;

import com.twb.pokerapp.web.exception.NotFoundException;
import com.twb.pokerapp.web.exception.ValidationException;
import com.twb.pokerapp.web.exception.validation.ApiErrorDTO;
import com.twb.pokerapp.web.exception.validation.ValidationDTO;
import com.twb.pokerapp.web.exception.validation.ValidationFieldDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationDTO> handleValidationException(ValidationException ex) {
        var validationDTO = new ValidationDTO();
        var fieldDTO = new ValidationFieldDTO();
        fieldDTO.setField(ex.getField());
        fieldDTO.setMessage(ex.getMessage());
        validationDTO.setFields(Collections.singletonList(fieldDTO));
        return new ResponseEntity<>(validationDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var fields = new ArrayList<ValidationFieldDTO>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage();
            var fieldDTO = new ValidationFieldDTO();
            fieldDTO.setField(fieldName);
            fieldDTO.setMessage(errorMessage);
            fields.add(fieldDTO);
        });
        var validationDTO = new ValidationDTO();
        validationDTO.setFields(fields);
        return new ResponseEntity<>(validationDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFoundException(NotFoundException ex) {
        var apiError = new ApiErrorDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(Exception ex) {
        var apiError = new ApiErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
