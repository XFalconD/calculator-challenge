package com.wit.calculator.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wit.calculator.dto.OperationRequest;
import com.wit.calculator.dto.OperationResponse;
import com.wit.calculator.dto.OperationType;
import com.wit.calculator.util.MDCUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculatorService {

    @Value("${calculator.precision:50}")
    private int precision;

    private final MDCUtil mdcUtil;

    public CalculatorService(MDCUtil mdcUtil) {
        this.mdcUtil = mdcUtil;
    }

    public OperationResponse process(OperationRequest request) {
        mdcUtil.setRequestId(request.getRequestId());
        log.info("Processing request: {}", request);

        BigDecimal a = request.getOperandA();
        BigDecimal b = request.getOperandB();
        OperationType op = request.getOperation();

        if (a == null || b == null) {
            return OperationResponse.builder()
                    .requestId(request.getRequestId())
                    .status("ERROR")
                    .errorMessage("Operands cannot be null")
                    .build();
        }

        try {
            BigDecimal result;
            switch (op) {
                case SUM:
                    result = a.add(b);
                    break;
                case SUBTRACT:
                    result = a.subtract(b);
                    break;
                case MULTIPLY:
                    result = a.multiply(b);
                    break;
                case DIVIDE:
                    if (b.compareTo(BigDecimal.ZERO) == 0) {
                        return OperationResponse.builder()
                                .requestId(request.getRequestId())
                                .status("ERROR")
                                .errorMessage("Division by zero")
                                .build();
                    }
                    // Use arbitrary precision with MathContext
                    result = a.divide(b, new MathContext(precision, RoundingMode.HALF_UP));
                    break;
                default:
                    return OperationResponse.builder()
                            .requestId(request.getRequestId())
                            .status("ERROR")
                            .errorMessage("Unknown operation " + op)
                            .build();
            }

            return OperationResponse.builder()
                    .requestId(request.getRequestId())
                    .result(result)
                    .status("SUCCESS")
                    .build();
        } catch (Exception e) {
            log.error("Error processing operation", e);
            return OperationResponse.builder()
                    .requestId(request.getRequestId())
                    .status("ERROR")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}
