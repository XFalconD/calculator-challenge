package com.wit.calculator.util;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MDCUtil {
    private static final String REQUEST_ID = "requestId";

    public void setRequestId(String requestId) {
        if (requestId != null) {
            MDC.put(REQUEST_ID, requestId);
        }
    }
}
