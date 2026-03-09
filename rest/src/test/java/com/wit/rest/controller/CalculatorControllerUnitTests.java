package com.wit.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.wit.rest.dto.ApiResponse;
import com.wit.rest.exception.InvalidOperandException;
import com.wit.rest.service.CalculatorService;
import com.wit.rest.util.MDCUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculatorController Unit Tests")
public class CalculatorControllerUnitTests {

    @Mock
    private CalculatorService calculatorService;

    @Mock
    private MDCUtil mdcUtil;

    @InjectMocks
    private CalculatorController calculatorController;

    @BeforeEach
    void setUp() {
        // lenient stub so that tests which throw before requesting the ID don't fail
        org.mockito.Mockito.lenient()
            .when(mdcUtil.getRequestId()).thenReturn("test-request-123");
    }

    @Test
    @DisplayName("Sum endpoint returns correct result")
    void testSum_ReturnsCorrectResult() {
        BigDecimal a = new BigDecimal("10.5");
        BigDecimal b = new BigDecimal("20.3");
        BigDecimal expectedResult = new BigDecimal("30.8");

        when(calculatorService.sum(a, b)).thenReturn(expectedResult);

        ResponseEntity<ApiResponse> response = calculatorController.sum(a, b);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, expectedResult.compareTo(response.getBody().getResult()));
        verify(calculatorService).sum(a, b);
    }

    @Test
    @DisplayName("Subtract endpoint returns correct result")
    void testSubtract_ReturnsCorrectResult() {
        BigDecimal a = new BigDecimal("50");
        BigDecimal b = new BigDecimal("20");
        BigDecimal expectedResult = new BigDecimal("30");

        when(calculatorService.subtract(a, b)).thenReturn(expectedResult);

        ResponseEntity<ApiResponse> response = calculatorController.subtract(a, b);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, expectedResult.compareTo(response.getBody().getResult()));
    }

    @Test
    @DisplayName("Multiply endpoint returns correct result")
    void testMultiply_ReturnsCorrectResult() {
        BigDecimal a = new BigDecimal("5");
        BigDecimal b = new BigDecimal("4");
        BigDecimal expectedResult = new BigDecimal("20");

        when(calculatorService.multiply(a, b)).thenReturn(expectedResult);

        ResponseEntity<ApiResponse> response = calculatorController.multiply(a, b);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, expectedResult.compareTo(response.getBody().getResult()));
    }

    @Test
    @DisplayName("Divide endpoint returns correct result")
    void testDivide_ReturnsCorrectResult() {
        BigDecimal a = new BigDecimal("100");
        BigDecimal b = new BigDecimal("4");
        BigDecimal expectedResult = new BigDecimal("25");

        when(calculatorService.divide(a, b)).thenReturn(expectedResult);

        ResponseEntity<ApiResponse> response = calculatorController.divide(a, b);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, expectedResult.compareTo(response.getBody().getResult()));
    }

    @Test
    @DisplayName("Response includes request ID header")
    void testResponseIncludesRequestIdHeader() {
        BigDecimal a = new BigDecimal("5");
        BigDecimal b = new BigDecimal("3");
        BigDecimal expectedResult = new BigDecimal("8");

        when(calculatorService.sum(a, b)).thenReturn(expectedResult);

        ResponseEntity<ApiResponse> response = calculatorController.sum(a, b);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test-request-123", response.getHeaders().getFirst("X-Request-ID"));
    }

    @Test
    @DisplayName("Sum endpoint with arbitrary precision decimals")
    void testSum_WithArbitraryPrecision() {
        BigDecimal a = new BigDecimal("123.456789123456789");
        BigDecimal b = new BigDecimal("987.654321987654321");
        BigDecimal expectedResult = new BigDecimal("1111.111111111111110");

        when(calculatorService.sum(a, b)).thenReturn(expectedResult);

        ResponseEntity<ApiResponse> response = calculatorController.sum(a, b);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, expectedResult.compareTo(response.getBody().getResult()));
    }

    @Test
    @DisplayName("Service throws exception propagates correctly")
    void testServiceException_PropagatesCorrectly() {
        BigDecimal a = new BigDecimal("10");
        BigDecimal b = new BigDecimal("0");

        when(calculatorService.divide(a, b))
            .thenThrow(new RuntimeException("Division by zero"));

        assertThrows(RuntimeException.class, () -> 
            calculatorController.divide(a, b)
        );
    }
}
