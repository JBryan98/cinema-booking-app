package com.jbryan98.bookingapp.booking.application;

import com.jbryan98.bookingapp.booking.api.dto.BookingRequest;
import com.jbryan98.bookingapp.booking.api.dto.BookingResponse;

import java.util.UUID;

public interface BookingService {
    BookingResponse create(BookingRequest request);

    BookingResponse findById(UUID id);

    BookingResponse confirm(UUID id);

    BookingResponse cancel(UUID id);
}
