package com.wit.calculator.util;

import java.util.UUID;

public class RequestIdGenerator {
    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
