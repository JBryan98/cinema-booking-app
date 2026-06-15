package com.jbryan98.bookingapp.orchestrator.application;

import com.jbryan98.bookingapp.orchestrator.api.dto.BookingPlacementResponse;
import com.jbryan98.bookingapp.orchestrator.api.dto.PlaceBookingRequest;

import java.util.UUID;

public interface SagaOrchestrator {
    BookingPlacementResponse placeBooking(PlaceBookingRequest request);

    BookingPlacementResponse cancelBooking(UUID bookingId);
}