package com.wit.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wit.rest.dto.OperationResponse;
import com.wit.rest.util.MDCUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaConsumerService Tests")
public class KafkaConsumerServiceTests {

    @Mock
    private MDCUtil mdcUtil;

    private KafkaConsumerService kafkaConsumerService;

    @BeforeEach
    void setUp() {
        kafkaConsumerService = new KafkaConsumerService(mdcUtil);
    }

    @Test
    @DisplayName("Should cache operation response with requestId")
    void testHandleOperationResponse_CachesResponse() {
        OperationResponse response = OperationResponse.builder()
            .requestId("test-request-123")
            .result(new BigDecimal("30"))
            .status("SUCCESS")
            .build();

        kafkaConsumerService.handleOperationResponse(response);

        assertTrue(kafkaConsumerService.hasResponse("test-request-123"));
        assertEquals(response, kafkaConsumerService.getResponse("test-request-123"));
    }

    @Test
    @DisplayName("Should retrieve cached response by requestId")
    void testGetResponse_ReturnsCachedResponse() {
        String requestId = "request-456";
        OperationResponse response = OperationResponse.builder()
            .requestId(requestId)
            .result(new BigDecimal("50"))
            .status("SUCCESS")
            .build();

        kafkaConsumerService.handleOperationResponse(response);

        OperationResponse retrieved = kafkaConsumerService.getResponse(requestId);
        assertNotNull(retrieved);
        assertEquals(requestId, retrieved.getRequestId());
        assertEquals(0, new BigDecimal("50").compareTo(retrieved.getResult()));
    }

    @Test
    @DisplayName("Should return null for non-existent requestId")
    void testGetResponse_ReturnsNullForMissingRequestId() {
        OperationResponse response = kafkaConsumerService.getResponse("non-existent-id");
        assertEquals(null, response);
    }

    @Test
    @DisplayName("Should remove response from cache")
    void testRemoveResponse_DeletesFromCache() {
        String requestId = "request-789";
        OperationResponse response = OperationResponse.builder()
            .requestId(requestId)
            .result(new BigDecimal("100"))
            .status("SUCCESS")
            .build();

        kafkaConsumerService.handleOperationResponse(response);
        assertTrue(kafkaConsumerService.hasResponse(requestId));

        kafkaConsumerService.removeResponse(requestId);
        assertFalse(kafkaConsumerService.hasResponse(requestId));
    }

    @Test
    @DisplayName("Should check response existence")
    void testHasResponse_ChecksExistence() {
        String requestId = "check-request-123";
        
        assertFalse(kafkaConsumerService.hasResponse(requestId));

        OperationResponse response = OperationResponse.builder()
            .requestId(requestId)
            .result(new BigDecimal("25"))
            .status("SUCCESS")
            .build();

        kafkaConsumerService.handleOperationResponse(response);
        assertTrue(kafkaConsumerService.hasResponse(requestId));
    }

    @Test
    @DisplayName("Should handle error responses")
    void testHandleOperationResponse_ErrorResponse() {
        OperationResponse errorResponse = OperationResponse.builder()
            .requestId("error-request-123")
            .status("ERROR")
            .errorMessage("Division by zero")
            .build();

        kafkaConsumerService.handleOperationResponse(errorResponse);

        assertTrue(kafkaConsumerService.hasResponse("error-request-123"));
        OperationResponse retrieved = kafkaConsumerService.getResponse("error-request-123");
        assertEquals("ERROR", retrieved.getStatus());
        assertEquals("Division by zero", retrieved.getErrorMessage());
    }

    @Test
    @DisplayName("Should propagate requestId to MDC when handling response")
    void testHandleOperationResponse_SetsMDC() {
        OperationResponse response = OperationResponse.builder()
            .requestId("mdc-request-123")
            .result(new BigDecimal("42"))
            .status("SUCCESS")
            .build();

        kafkaConsumerService.handleOperationResponse(response);

        verify(mdcUtil).setRequestId("mdc-request-123");
    }

    @Test
    @DisplayName("Should handle multiple concurrent responses")
    void testHandleOperationResponse_MultipleConcurrentResponses() {
        OperationResponse response1 = OperationResponse.builder()
            .requestId("request-1")
            .result(new BigDecimal("10"))
            .status("SUCCESS")
            .build();

        OperationResponse response2 = OperationResponse.builder()
            .requestId("request-2")
            .result(new BigDecimal("20"))
            .status("SUCCESS")
            .build();

        kafkaConsumerService.handleOperationResponse(response1);
        kafkaConsumerService.handleOperationResponse(response2);

        assertTrue(kafkaConsumerService.hasResponse("request-1"));
        assertTrue(kafkaConsumerService.hasResponse("request-2"));
        assertEquals(0, new BigDecimal("10").compareTo(kafkaConsumerService.getResponse("request-1").getResult()));
        assertEquals(0, new BigDecimal("20").compareTo(kafkaConsumerService.getResponse("request-2").getResult()));
    }

    @Test
    @DisplayName("Should handle response with arbitrary precision result")
    void testHandleOperationResponse_ArbitraryPrecision() {
        BigDecimal preciseResult = new BigDecimal("123.456789123456789");
        OperationResponse response = OperationResponse.builder()
            .requestId("precise-request")
            .result(preciseResult)
            .status("SUCCESS")
            .build();

        kafkaConsumerService.handleOperationResponse(response);

        OperationResponse retrieved = kafkaConsumerService.getResponse("precise-request");
        assertEquals(preciseResult, retrieved.getResult());
    }
}
