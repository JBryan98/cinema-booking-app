package com.jbryan98.bookingapp.orchestrator.exception;

public class SagaException extends RuntimeException {
    public SagaException(String message, Throwable cause) {
        super(message, cause);
    }
}
