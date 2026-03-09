package com.wit.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.wit.rest.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidOperandException.class)
    public ResponseEntity<ApiResponse> handleInvalidOperandException(InvalidOperandException ex) {
        log.error("Invalid operand: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
            ApiResponse.builder()
                .errorMessage(ex.getMessage())
                .build()
        );
    }

    @ExceptionHandler(CalculatorTimeoutException.class)
    public ResponseEntity<ApiResponse> handleCalculatorTimeoutException(CalculatorTimeoutException ex) {
        log.error("Calculator timeout: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
            ApiResponse.builder()
                .errorMessage(ex.getMessage())
                .build()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Calculator error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
            ApiResponse.builder()
                .errorMessage(ex.getMessage())
                .build()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("Missing required parameter: {}", ex.getParameterName());
        String message = String.format("Missing required parameter: '%s'", ex.getParameterName());
        return ResponseEntity.badRequest().body(
            ApiResponse.builder()
                .errorMessage(message)
                .build()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Invalid parameter type: {}", ex.getMessage());
        String message = String.format("Invalid value for parameter '%s': expected %s but got '%s'", 
            ex.getName(), ex.getRequiredType().getSimpleName(), ex.getValue());
        return ResponseEntity.badRequest().body(
            ApiResponse.builder()
                .errorMessage(message)
                .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.builder()
                .errorMessage("An unexpected error occurred")
                .build()
        );
    }
}
