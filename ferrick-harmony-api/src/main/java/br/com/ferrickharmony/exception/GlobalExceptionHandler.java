package br.com.ferrickharmony.exception;

import br.com.ferrickharmony.exception.dto.ApiErrorResponse;
import br.com.ferrickharmony.exception.dto.DataErrorValidation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String RESOURCE_NOT_FOUND = "Resource not found.";
    private static final String VALIDATION_ERROR = "Validation error in the provided data.";
    private static final String INTERNAL_ERROR = "An internal server error occurred. Please try again later.";

    private final MessageSource messageSource;

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
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        String translatedMessage = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                ex.getMessage(),
                LocaleContextHolder.getLocale()
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", translatedMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
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