package com.jbryan98.bookingapp.movie.exception;

public class AvailableSeatsExceedsTotalSeatsException extends RuntimeException {
    public AvailableSeatsExceedsTotalSeatsException(String message) {
        super(message);
    }
}
