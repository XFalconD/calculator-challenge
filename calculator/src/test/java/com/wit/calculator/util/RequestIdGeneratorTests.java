package com.wit.calculator.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RequestIdGenerator Tests")
public class RequestIdGeneratorTests {

    private RequestIdGenerator requestIdGenerator;

    @BeforeEach
    void setUp() {
        requestIdGenerator = new RequestIdGenerator();
    }

    @Test
    @DisplayName("Should generate non-null request ID")
    void testGenerateRequestId_NotNull() {
        String requestId = requestIdGenerator.generateRequestId();
        assertNotNull(requestId);
    }

    @Test
    @DisplayName("Should generate non-empty request ID")
    void testGenerateRequestId_NotEmpty() {
        String requestId = requestIdGenerator.generateRequestId();
        assertTrue(requestId.length() > 0);
    }

    @Test
    @DisplayName("Should generate unique request IDs")
    void testGenerateRequestId_Unique() {
        String id1 = requestIdGenerator.generateRequestId();
        String id2 = requestIdGenerator.generateRequestId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertTrue(!id1.equals(id2), "Generated IDs should be unique");
    }

    @Test
    @DisplayName("Should generate UUID format request IDs")
    void testGenerateRequestId_UUIDFormat() {
        String requestId = requestIdGenerator.generateRequestId();

        assertTrue(requestId.matches(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"),
            "Generated ID should be in UUID format");
    }

    @Test
    @DisplayName("Should generate multiple unique IDs in sequence")
    void testGenerateRequestId_MultipleUniqueIds() {
        java.util.Set<String> ids = new java.util.HashSet<>();
        for (int i = 0; i < 100; i++) {
            ids.add(requestIdGenerator.generateRequestId());
        }
        assertTrue(ids.size() == 100, "All 100 generated IDs should be unique");
    }
}
