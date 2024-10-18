package com.mitar.dipl.exception;

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
                "Invalid email or password",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle DisabledException
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledException(DisabledException ex, WebRequest request) {
        logger.warn("DisabledException: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "User account is disabled",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle UsernameNotFoundException
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        logger.warn("UsernameNotFoundException: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "User not found",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.warn("AccessDeniedException: {}", ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.FORBIDDEN,
                "Access is denied",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        logger.warn("MethodArgumentNotValidException: {}", errors);

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                errors
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        logger.warn("ConstraintViolationException: {}", errors);

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                errors
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle DataIntegrityViolationException
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        Throwable cause = ex.getCause();
        String errorMessage = "Database integrity violation.";
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            errorMessage += " " + cause.getMessage();
        } else {
            errorMessage += " " + ex.getMessage();
        }

        logger.warn("DataIntegrityViolationException: {}", errorMessage);

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Database Error",
                errorMessage
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error("Unhandled exception occurred: ", ex);
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
