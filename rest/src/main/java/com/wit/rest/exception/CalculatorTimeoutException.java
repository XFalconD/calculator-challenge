package com.wit.rest.exception;

public class CalculatorTimeoutException extends RuntimeException {
    public CalculatorTimeoutException(String message) {
        super(message);
    }

    public CalculatorTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
