package com.wit.rest.exception;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.wit.rest.dto.ApiResponse;

@DisplayName("GlobalExceptionHandler Unit Tests")
public class GlobalExceptionHandlerUnitTests {

    private GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Handles InvalidOperandException with BAD_REQUEST status")
    void testHandleInvalidOperandException() {
        var ex = new InvalidOperandException("Invalid operand: null value");

        ResponseEntity<ApiResponse> response = exceptionHandler.handleInvalidOperandException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid operand: null value", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Handles RuntimeException with BAD_REQUEST status")
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Calculator error");

        ResponseEntity<ApiResponse> response = exceptionHandler.handleRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Calculator error", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Handles general Exception with INTERNAL_SERVER_ERROR status")
    void testHandleGeneralException() {
        Exception ex = new RuntimeException("Unexpected error occurred");

        ResponseEntity<ApiResponse> response = exceptionHandler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Handles MissingServletRequestParameterException with BAD_REQUEST status")
    void testHandleMissingServletRequestParameterException() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("a", "BigDecimal");

        ResponseEntity<ApiResponse> response = exceptionHandler.handleMissingServletRequestParameterException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing required parameter: 'a'", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Handles MethodArgumentTypeMismatchException with BAD_REQUEST status")
    void testHandleMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("abc", BigDecimal.class, "a", null, new NumberFormatException("For input string: \"abc\""));

        ResponseEntity<ApiResponse> response = exceptionHandler.handleMethodArgumentTypeMismatchException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid value for parameter 'a': expected BigDecimal but got 'abc'", response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Exception handler returns ApiResponse with proper structure")
    void testExceptionHandlerResponseStructure() {
        InvalidOperandException ex = new InvalidOperandException("Test error");

        ResponseEntity<ApiResponse> response = exceptionHandler.handleInvalidOperandException(ex);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrorMessage());
    }

    @Test
    @DisplayName("Handles different exception messages correctly")
    void testHandleDifferentExceptionMessages() {
        String[] messages = {
            "Operand 'a' cannot be null",
            "Value exceeds maximum precision",
            "Invalid number format"
        };

        for (String message : messages) {
            InvalidOperandException ex = new InvalidOperandException(message);
            ResponseEntity<ApiResponse> response = exceptionHandler.handleInvalidOperandException(ex);

            assertEquals(message, response.getBody().getErrorMessage());
        }
    }
}
