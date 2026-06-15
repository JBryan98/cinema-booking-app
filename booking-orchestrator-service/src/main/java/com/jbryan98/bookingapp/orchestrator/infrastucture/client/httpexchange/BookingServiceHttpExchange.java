package com.jbryan98.bookingapp.orchestrator.infrastucture.client.httpexchange;

import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.BookingRequest;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

@HttpExchange("/bookings")
public interface BookingServiceHttpExchange {
    @PostExchange
    BookingResponse createBooking(@Valid @RequestBody BookingRequest request);

    @PatchExchange("/{id}/cancel")
    BookingResponse cancelBooking(@PathVariable(name = "id") UUID bookingId);

    @PatchExchange("/{id}/confirm")
    BookingResponse confirmBooking(@PathVariable(name = "id") UUID bookingId);
}
