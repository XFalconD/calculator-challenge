package com.wit.calculator.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.wit.calculator.dto.OperationRequest;
import com.wit.calculator.dto.OperationResponse;
import com.wit.calculator.dto.OperationType;
import com.wit.calculator.util.MDCUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculatorKafkaListener Tests")
public class CalculatorKafkaListenerTests {

    @Mock
    private CalculatorService calculatorService;

    @Mock
    private KafkaTemplate<String, OperationResponse> kafkaTemplate;

    @Mock
    private MDCUtil mdcUtil;

    private CalculatorKafkaListener listener;

    @BeforeEach
    void setUp() {
        listener = new CalculatorKafkaListener(calculatorService, kafkaTemplate, mdcUtil, "calculator-responses");
    }

    @Test
    @DisplayName("Should process incoming request and send response")
    void testListen_SendsResponse() {
        OperationRequest request = OperationRequest.builder()
                .requestId("req-123")
                .operation(OperationType.SUM)
                .operandA(new BigDecimal("1"))
                .operandB(new BigDecimal("2"))
                .build();

        OperationResponse response = OperationResponse.builder()
                .requestId("req-123")
                .result(new BigDecimal("3"))
                .status("SUCCESS")
                .build();

        when(calculatorService.process(request)).thenReturn(response);

        listener.listen(request);

        verify(mdcUtil).setRequestId("req-123");
        verify(calculatorService).process(request);
        verify(kafkaTemplate).send(eq("calculator-responses"), eq("req-123"), eq(response));
    }
}
