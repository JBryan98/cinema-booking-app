package com.jbryan98.bookingapp.orchestrator.api;

import com.jbryan98.bookingapp.orchestrator.api.dto.BookingPlacementResponse;
import com.jbryan98.bookingapp.orchestrator.api.dto.PlaceBookingRequest;
import com.jbryan98.bookingapp.orchestrator.application.SagaOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/booking-placements")
@RequiredArgsConstructor
public class BookingPlacementController {

    private final SagaOrchestrator orchestrator;

    @PostMapping
    public ResponseEntity<BookingPlacementResponse> placeBooking(@Valid @RequestBody PlaceBookingRequest request) {
        var response = orchestrator.placeBooking(request);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.bookingId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingPlacementResponse> cancelBooking(@PathVariable(name = "id") UUID bookingId) {
        return ResponseEntity.ok(orchestrator.cancelBooking(bookingId));
    }
}
