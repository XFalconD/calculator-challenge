package com.wit.rest.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

@DisplayName("MDCUtil Tests")
public class MDCUtilTests {

    private MDCUtil mdcUtil;

    @BeforeEach
    void setUp() {
        mdcUtil = new MDCUtil();
        MDC.clear();
    }

    @Test
    @DisplayName("Should set request ID in MDC")
    void testSetRequestId() {
        String requestId = "test-request-123";
        mdcUtil.setRequestId(requestId);

        assertEquals(requestId, MDC.get("requestId"));
    }

    @Test
    @DisplayName("Should retrieve request ID from MDC")
    void testGetRequestId() {
        String requestId = "retrieve-test-456";
        mdcUtil.setRequestId(requestId);

        assertEquals(requestId, mdcUtil.getRequestId());
    }

    @Test
    @DisplayName("Should return null for missing request ID")
    void testGetRequestId_NotSet() {
        MDC.clear();
        assertNull(mdcUtil.getRequestId());
    }

    @Test
    @DisplayName("Should clear MDC")
    void testClear() {
        mdcUtil.setRequestId("to-be-cleared");
        mdcUtil.clear();

        assertNull(mdcUtil.getRequestId());
    }

    @Test
    @DisplayName("Should overwrite existing request ID")
    void testSetRequestId_Overwrite() {
        String id1 = "first-id";
        String id2 = "second-id";

        mdcUtil.setRequestId(id1);
        assertEquals(id1, mdcUtil.getRequestId());

        mdcUtil.setRequestId(id2);
        assertEquals(id2, mdcUtil.getRequestId());
    }

    @Test
    @DisplayName("Should handle multiple MDC operations")
    void testMultipleMDCOperations() {
        String id1 = "multi-id-1";
        String id2 = "multi-id-2";

        mdcUtil.setRequestId(id1);
        assertEquals(id1, mdcUtil.getRequestId());

        mdcUtil.setRequestId(id2);
        assertEquals(id2, mdcUtil.getRequestId());

        mdcUtil.clear();
        assertNull(mdcUtil.getRequestId());

        mdcUtil.setRequestId(id1);
        assertEquals(id1, mdcUtil.getRequestId());
    }
}
