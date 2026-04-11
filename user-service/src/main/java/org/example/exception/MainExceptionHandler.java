package org.example.exception;

import org.example.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Main exception handler that intercepts exceptions thrown by controllers.
 * Formats error details into a consistent {@link ErrorResponse} structure.
 */
@RestControllerAdvice
public class MainExceptionHandler {

    /**
     * Handles validation errors triggered by {@code @Valid} annotations on request bodies.
     *
     * @param ex the exception containing details about the failed validation.
     * @return an {@link ErrorResponse} containing a map of field-specific error messages.
     */
   @ExceptionHandler(MethodArgumentNotValidException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации данных",
                errors
        );
    }

    /**
     * Handles general business logic errors and runtime exceptions.
     *
     * @param ex the runtime exception thrown during service execution.
     * @return a {@link ResponseEntity} containing an {@link ErrorResponse} and HTTP 404 (Not Found).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
