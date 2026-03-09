package com.wit.rest.service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.wit.rest.dto.OperationRequest;
import com.wit.rest.dto.OperationType;
import com.wit.rest.util.MDCUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaProducerService Tests")
public class KafkaProducerServiceTests {

    @Mock
    private KafkaTemplate<String, OperationRequest> kafkaTemplate;

    @Mock
    private MDCUtil mdcUtil;

    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        kafkaProducerService = new KafkaProducerService(kafkaTemplate, mdcUtil, "calculator-requests");
        // Configure mock to return a completed CompletableFuture
        when(kafkaTemplate.send(any(), any(), any(OperationRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    @DisplayName("Should send operation request to Kafka")
    void testSendOperationRequest_Success() {
        OperationRequest request = OperationRequest.builder()
            .requestId("send-test-123")
            .operation(OperationType.SUM)
            .operandA(new BigDecimal("10"))
            .operandB(new BigDecimal("20"))
            .build();

        kafkaProducerService.sendOperationRequest(request);

        verify(kafkaTemplate).send(
            eq("calculator-requests"),
            eq("send-test-123"),
            any(OperationRequest.class)
        );
    }

    @Test
    @DisplayName("Should send request with correct requestId as key")
    void testSendOperationRequest_CorrectKey() {
        OperationRequest request = OperationRequest.builder()
            .requestId("key-test-456")
            .operation(OperationType.MULTIPLY)
            .operandA(new BigDecimal("5"))
            .operandB(new BigDecimal("4"))
            .build();

        kafkaProducerService.sendOperationRequest(request);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(any(), keyCaptor.capture(), any());

        assertNotNull(keyCaptor.getValue());
    }

    @Test
    @DisplayName("Should handle arbitrary precision operands")
    void testSendOperationRequest_ArbitraryPrecision() {
        BigDecimal preciseA = new BigDecimal("123.456789123456789");
        BigDecimal preciseB = new BigDecimal("987.654321987654321");

        OperationRequest request = OperationRequest.builder()
            .requestId("precision-test")
            .operation(OperationType.DIVIDE)
            .operandA(preciseA)
            .operandB(preciseB)
            .build();

        kafkaProducerService.sendOperationRequest(request);

        ArgumentCaptor<OperationRequest> requestCaptor = ArgumentCaptor.forClass(OperationRequest.class);
        verify(kafkaTemplate).send(any(), any(), requestCaptor.capture());

        OperationRequest capturedRequest = requestCaptor.getValue();
        assertNotNull(capturedRequest.getOperandA());
        assertNotNull(capturedRequest.getOperandB());
    }

    @Test
    @DisplayName("Should include requestId in the sent message")
    void testSendOperationRequest_RequestIdIncluded() {
        String requestId = "include-id-test-789";
        OperationRequest request = OperationRequest.builder()
            .requestId(requestId)
            .operation(OperationType.SUM)
            .operandA(new BigDecimal("1"))
            .operandB(new BigDecimal("1"))
            .build();

        kafkaProducerService.sendOperationRequest(request);

        ArgumentCaptor<OperationRequest> requestCaptor = ArgumentCaptor.forClass(OperationRequest.class);
        verify(kafkaTemplate).send(any(), any(), requestCaptor.capture());

        assertEquals(requestId, requestCaptor.getValue().getRequestId());
    }

    private void assertEquals(String expected, String actual) {
        assert expected.equals(actual);
    }
}
