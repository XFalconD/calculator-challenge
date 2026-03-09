package com.wit.rest.service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wit.rest.dto.OperationRequest;
import com.wit.rest.dto.OperationResponse;
import com.wit.rest.dto.OperationType;
import com.wit.rest.exception.CalculatorTimeoutException;
import com.wit.rest.exception.InvalidOperandException;
import com.wit.rest.util.MDCUtil;
import com.wit.rest.util.RequestIdGenerator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculatorService {

    @Value("${calculator.request.timeout:30}")
    private long requestTimeout;

    @Value("${calculator.request.timeout.unit:SECONDS}")
    private String requestTimeoutUnit;

    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;
    private final RequestIdGenerator requestIdGenerator;
    private final MDCUtil mdcUtil;

    public CalculatorService(
            KafkaProducerService kafkaProducerService,
            KafkaConsumerService kafkaConsumerService,
            RequestIdGenerator requestIdGenerator,
            MDCUtil mdcUtil) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerService = kafkaConsumerService;
        this.requestIdGenerator = requestIdGenerator;
        this.mdcUtil = mdcUtil;
    }

    public BigDecimal sum(BigDecimal a, BigDecimal b) {
        return executeOperation(OperationType.SUM, a, b);
    }

    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return executeOperation(OperationType.SUBTRACT, a, b);
    }

    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return executeOperation(OperationType.MULTIPLY, a, b);
    }

    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        return executeOperation(OperationType.DIVIDE, a, b);
    }

    private BigDecimal executeOperation(OperationType operation, BigDecimal operandA, BigDecimal operandB) {
        validateOperands(operandA, operandB);

        String requestId = requestIdGenerator.generateRequestId();
        mdcUtil.setRequestId(requestId);

        log.info("Executing operation: operation={}, operandA={}, operandB={}", operation, operandA, operandB);

        OperationRequest request = OperationRequest.builder()
            .requestId(requestId)
            .operation(operation)
            .operandA(operandA)
            .operandB(operandB)
            .build();

        kafkaProducerService.sendOperationRequest(request);

        try {
            OperationResponse response = waitForResponse(requestId);

            if (response == null) {
                throw new CalculatorTimeoutException(
                    String.format("No response received from calculator for request %s within %d %s",
                        requestId, requestTimeout, requestTimeoutUnit));
            }

            if ("ERROR".equals(response.getStatus())) {
                throw new RuntimeException("Calculator error: " + response.getErrorMessage());
            }

            log.info("Operation completed successfully: result={}", response.getResult());
            return response.getResult();
        } finally {
            kafkaConsumerService.removeResponse(requestId);
        }
    }

    private OperationResponse waitForResponse(String requestId) throws CalculatorTimeoutException {
        long startTime = System.currentTimeMillis();
        TimeUnit unit = TimeUnit.valueOf(requestTimeoutUnit);
        long timeoutMillis = unit.toMillis(requestTimeout);

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            OperationResponse response = kafkaConsumerService.getResponse(requestId);
            if (response != null) {
                return response;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CalculatorTimeoutException("Interrupted while waiting for response", e);
            }
        }

        throw new CalculatorTimeoutException(
            String.format("Timeout waiting for response for request %s", requestId));
    }

    private void validateOperands(BigDecimal a, BigDecimal b) {
        if (a == null) {
            throw new InvalidOperandException("Operand 'a' cannot be null");
        }
        if (b == null) {
            throw new InvalidOperandException("Operand 'b' cannot be null");
        }
    }
}
