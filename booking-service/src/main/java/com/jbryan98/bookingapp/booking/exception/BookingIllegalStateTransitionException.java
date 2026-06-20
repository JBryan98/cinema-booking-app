package com.jbryan98.bookingapp.booking.exception;

public class BookingIllegalStateTransitionException extends RuntimeException {
    public BookingIllegalStateTransitionException(String message) {
        super(message);
    }
}
