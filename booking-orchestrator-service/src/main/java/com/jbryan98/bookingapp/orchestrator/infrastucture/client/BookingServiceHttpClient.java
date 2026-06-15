package com.jbryan98.bookingapp.orchestrator.infrastucture.client;

import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.BookingRequest;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.BookingResponse;

import java.util.UUID;

public interface BookingServiceHttpClient {
    BookingResponse createBooking(BookingRequest request);

    BookingResponse confirmBooking(UUID bookingId);

    BookingResponse cancelBooking(UUID bookingId);
}
