package com.jbryan98.bookingapp.orchestrator.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record PlaceBookingRequest(
        @NotNull(message = "screeningId is required.")
        UUID screeningId,

        @NotNull(message = "customerId is required.")
        UUID customerId,

        @NotNull(message = "totalAmount is required.")
        @PositiveOrZero(message = "totalAmount must be zero or positive.")
        BigDecimal totalAmount
) {
}
