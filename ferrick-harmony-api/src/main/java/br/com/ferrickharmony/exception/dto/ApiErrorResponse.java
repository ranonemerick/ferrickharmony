package br.com.ferrickharmony.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String message,
        String path,
        List<DataErrorValidation> fieldErrors
) {}
