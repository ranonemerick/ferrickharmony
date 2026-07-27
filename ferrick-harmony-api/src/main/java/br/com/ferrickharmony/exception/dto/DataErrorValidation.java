package br.com.ferrickharmony.exception.dto;

import org.springframework.validation.FieldError;

public record DataErrorValidation(String field,
                                  String message) {

    public DataErrorValidation (FieldError fieldError){
        this(fieldError.getField(), fieldError.getDefaultMessage());
    }

}
