package com.wit.rest.exception;

public class InvalidOperandException extends RuntimeException {
    public InvalidOperandException(String message) {
        super(message);
    }

    public InvalidOperandException(String message, Throwable cause) {
        super(message, cause);
    }
}
