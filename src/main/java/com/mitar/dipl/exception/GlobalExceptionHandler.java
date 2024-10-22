package com.mitar.dipl.exception;

import com.mitar.dipl.exception.custom.BadRequestException;
import com.mitar.dipl.exception.custom.ConflictException;
import com.mitar.dipl.exception.custom.InvalidUUIDException;
import com.mitar.dipl.exception.custom.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle BadCredentialsException
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        logger.warn("BadCredentialsException: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password.",
                "Please check your credentials and try again."
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle DisabledException
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledException(DisabledException ex, WebRequest request) {
        logger.warn("DisabledException: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "User account is disabled.",
                "Please contact support for assistance."
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle UsernameNotFoundException
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        logger.warn("UsernameNotFoundException: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "User not found.",
                "The user with the provided details does not exist."
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.warn("AccessDeniedException: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.FORBIDDEN,
                "Access is denied.",
                "You do not have the necessary permissions to perform this action."
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> capitalize(error.getField()) + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        logger.warn("MethodArgumentNotValidException: {}", errors);

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation Failed.",
                errors
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> capitalize(violation.getPropertyPath().toString()) + ": " + violation.getMessage())
                .collect(Collectors.toList());

        logger.warn("ConstraintViolationException: {}", errors);

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation Error.",
                errors
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle DataIntegrityViolationException
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        // Log the full stack trace for internal debugging
        logger.error("DataIntegrityViolationException: ", ex);

        String userMessage = "A data integrity violation occurred.";
        String developerMessage = "Database Error: " + extractMessage(ex);

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                userMessage,
                developerMessage // Optionally, you might want to omit this or include a more generic message
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle InvalidUUIDException
    @ExceptionHandler(InvalidUUIDException.class)
    public ResponseEntity<ApiError> handleInvalidUUIDException(InvalidUUIDException ex, WebRequest request) {
        logger.warn("InvalidUUIDException: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Invalid UUID.",
                "The provided identifier is not in a valid format."
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Handle ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(error, error.getStatus());
    }

    // Handle BadRequestException
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(error, error.getStatus());
    }

    // Handle ConflictException
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        ApiError error = new ApiError(HttpStatus.CONFLICT, ex.getMessage(), List.of(ex.getMessage()));
        return new ResponseEntity<>(error, error.getStatus());
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error("Unhandled exception occurred: ", ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                "Please try again later or contact support."
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Utility method to extract meaningful messages from exceptions
    private String extractMessage(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            String message = cause.getMessage();
            if (message.contains("Duplicate entry")) {
                String[] parts = message.split("Duplicate entry '");
                if (parts.length > 1) {
                    String entryPart = parts[1];
                    String[] entryParts = entryPart.split("'");
                    if (entryParts.length > 0) {
                        String duplicateValue = entryParts[0];
                        if (message.contains("for key 'tables.UKfjmmqyocmsfsje61iybqifd96'")) {
                            return "A table with number '" + duplicateValue + "' already exists.";
                        }
                        // Add more specific messages based on different constraints as needed
                    }
                }
            }
            // Handle other types of constraint violations if necessary
            return "Database integrity violation: " + message;
        }
        return ex.getMessage();
    }

    // Utility method to capitalize the first letter
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
