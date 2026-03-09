package com.wit.calculator.util;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @DisplayName("Should set requestId in MDC when provided")
    void testSetRequestId() {
        String id = "abc-123";
        mdcUtil.setRequestId(id);
        assertTrue(id.equals(MDC.get("requestId")));
    }

    @Test
    @DisplayName("Should not set MDC entry when requestId is null")
    void testSetRequestId_Null() {
        mdcUtil.setRequestId(null);
        assertNull(MDC.get("requestId"));
    }
}
