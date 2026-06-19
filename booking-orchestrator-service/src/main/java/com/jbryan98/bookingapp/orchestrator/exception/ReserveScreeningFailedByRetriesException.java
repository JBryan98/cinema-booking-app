package com.jbryan98.bookingapp.orchestrator.exception;

public class ReserveScreeningFailedByRetriesException extends RuntimeException {
    public ReserveScreeningFailedByRetriesException(String message, Throwable cause) {
        super(message, cause);
    }
}
