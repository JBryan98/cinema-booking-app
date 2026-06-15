package com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingRequest(
        @NotNull(message = "screeningId is required.")
        UUID screeningId,

        @NotNull(message = "customerId is required.")
        UUID customerId,

        @NotBlank(message = "movieTitle is required.")
        String movieTitle,

        @NotNull(message = "totalAmount is required.")
        @PositiveOrZero(message = "totalAmount must be zero or positive.")
        BigDecimal totalAmount
) {
}
