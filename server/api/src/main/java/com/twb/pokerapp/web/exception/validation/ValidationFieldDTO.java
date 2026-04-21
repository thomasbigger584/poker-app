package com.twb.pokerapp.web.exception.validation;

import lombok.Data;

@Data
public class ValidationFieldDTO {
    private String field;
    private String message;
}
