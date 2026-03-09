package com.wit.rest.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class RequestIdGenerator {
    
    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
