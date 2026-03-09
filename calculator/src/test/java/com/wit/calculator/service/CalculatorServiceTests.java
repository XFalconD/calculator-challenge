package com.wit.calculator.service;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wit.calculator.dto.OperationRequest;
import com.wit.calculator.dto.OperationResponse;
import com.wit.calculator.dto.OperationType;
import com.wit.calculator.util.MDCUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculatorService Tests")
public class CalculatorServiceTests {

    private CalculatorService calculatorService;
    private MDCUtil mdcUtil;

    @BeforeEach
    void setUp() {
        mdcUtil = new MDCUtil();
        calculatorService = new CalculatorService(mdcUtil);
    }

    @Test
    @DisplayName("Should correctly add two numbers")
    void testSum() {
        OperationRequest request = OperationRequest.builder()
                .requestId("req-1")
                .operation(OperationType.SUM)
                .operandA(new BigDecimal("2"))
                .operandB(new BigDecimal("3"))
                .build();

        OperationResponse response = calculatorService.process(request);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, new BigDecimal("5").compareTo(response.getResult()));
    }

    @Test
    @DisplayName("Should correctly subtract two numbers")
    void testSubtract() {
        OperationRequest request = OperationRequest.builder()
                .requestId("req-2")
                .operation(OperationType.SUBTRACT)
                .operandA(new BigDecimal("10"))
                .operandB(new BigDecimal("4"))
                .build();

        OperationResponse response = calculatorService.process(request);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, new BigDecimal("6").compareTo(response.getResult()));
    }

    @Test
    @DisplayName("Should correctly multiply two numbers")
    void testMultiply() {
        OperationRequest request = OperationRequest.builder()
                .requestId("req-3")
                .operation(OperationType.MULTIPLY)
                .operandA(new BigDecimal("3"))
                .operandB(new BigDecimal("5"))
                .build();

        OperationResponse response = calculatorService.process(request);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, new BigDecimal("15").compareTo(response.getResult()));
    }

    @Test
    @DisplayName("Should correctly divide two numbers")
    void testDivide() {
        OperationRequest request = OperationRequest.builder()
                .requestId("req-4")
                .operation(OperationType.DIVIDE)
                .operandA(new BigDecimal("10"))
                .operandB(new BigDecimal("2"))
                .build();

        OperationResponse response = calculatorService.process(request);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, new BigDecimal("5").compareTo(response.getResult()));
    }

    @Test
    @DisplayName("Should return error status for division by zero")
    void testDivideByZero() {
        OperationRequest request = OperationRequest.builder()
                .requestId("req-5")
                .operation(OperationType.DIVIDE)
                .operandA(new BigDecimal("10"))
                .operandB(BigDecimal.ZERO)
                .build();

        OperationResponse response = calculatorService.process(request);
        assertEquals("ERROR", response.getStatus());
        assertEquals("Division by zero", response.getErrorMessage());
    }

    @Test
    @DisplayName("Should return error when operands are null")
    void testNullOperands() {
        OperationRequest request = OperationRequest.builder()
                .requestId("req-6")
                .operation(OperationType.SUM)
                .operandA(null)
                .operandB(null)
                .build();

        OperationResponse response = calculatorService.process(request);
        assertEquals("ERROR", response.getStatus());
        assertEquals("Operands cannot be null", response.getErrorMessage());
    }

    @Test
    @DisplayName("Zero divided by a decimal returns plain zero (scale 0)")
    void testZeroDividedByDecimal() {
        OperationRequest request = OperationRequest.builder()
                .requestId("req-7")
                .operation(OperationType.DIVIDE)
                .operandA(BigDecimal.ZERO)
                .operandB(new BigDecimal("0.10000"))
                .build();
        
        OperationResponse response = calculatorService.process(request);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(0, response.getResult().compareTo(BigDecimal.ZERO));
        // stripTrailingZeros should yield scale 0 for a zero value
        assertEquals(0, response.getResult().scale());
    }

    @Test
    @DisplayName("Should support arbitrary precision numbers")
    void testArbitraryPrecision() {
        BigDecimal a = new BigDecimal("123.456789123456789");
        BigDecimal b = new BigDecimal("0.000000000000001");

        OperationRequest request = OperationRequest.builder()
                .requestId("req-8")
                .operation(OperationType.SUM)
                .operandA(a)
                .operandB(b)
                .build();

        OperationResponse response = calculatorService.process(request);
        assertEquals("SUCCESS", response.getStatus());
        assertNotNull(response.getResult());
    }
}
