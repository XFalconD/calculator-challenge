package com.wit.rest.util;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MDCUtil {
    
    private static final String REQUEST_ID_KEY = "requestId";
    
    public void setRequestId(String requestId) {
        MDC.put(REQUEST_ID_KEY, requestId);
    }
    
    public String getRequestId() {
        return MDC.get(REQUEST_ID_KEY);
    }
    
    public void clear() {
        MDC.clear();
    }
}
