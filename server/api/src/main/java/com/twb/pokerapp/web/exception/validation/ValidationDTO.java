package com.twb.pokerapp.web.exception.validation;

import lombok.Data;

import java.util.List;

@Data
public class ValidationDTO {
    private List<ValidationFieldDTO> fields;
}
