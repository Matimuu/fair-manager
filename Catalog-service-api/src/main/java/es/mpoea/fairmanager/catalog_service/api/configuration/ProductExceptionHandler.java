package es.mpoea.fairmanager.catalog_service.api.configuration;

import es.mpoea.fairmanager.catalog_service.api.exceptions.ProductNotFoundException;
import es.mpoea.fairmanager.catalog_service.api.exceptions.ProductsNotExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for the Product Service API. This class will handle exceptions thrown by the controllers and return appropriate HTTP responses.
 */
@RestControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));

        ValidationErrorResponse response = new ValidationErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "One or more fields failed validation. Please check the errors and try again.",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ProductsNotExistsException.class)
    public ResponseEntity<ValidationErrorResponse> handleProductsNotExistsException(
            ProductsNotExistsException ex,
            HttpServletRequest request
    ) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleProductNotFoundException(
            ProductNotFoundException ex,
            HttpServletRequest request
    ) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    public record ValidationErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> errors
    ) {
    }
}
