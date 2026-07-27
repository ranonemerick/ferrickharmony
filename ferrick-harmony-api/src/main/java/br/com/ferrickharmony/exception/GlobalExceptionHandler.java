package br.com.ferrickharmony.exception;

import br.com.ferrickharmony.exception.dto.ApiErrorResponse;
import br.com.ferrickharmony.exception.dto.DataErrorValidation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String RESOURCE_NOT_FOUND = "Resource not found.";
    private static final String VALIDATION_ERROR = "Validation error in the provided data.";
    private static final String INTERNAL_ERROR = "An internal server error occurred. Please try again later.";

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(HttpServletRequest request) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                RESOURCE_NOT_FOUND,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<DataErrorValidation> fieldErrors = ex.getFieldErrors().stream()
                .map(DataErrorValidation::new)
                .toList();

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                VALIDATION_ERROR,
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected server error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_ERROR,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private ApiErrorResponse buildErrorResponse(
            HttpStatus status,
            String message,
            String path,
            List<DataErrorValidation> errors) {

        return new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                message,
                path,
                errors
        );
    }
}