package com.jbryan98.bookingapp.orchestrator.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingPlacementResponse(
        UUID bookingId,
        UUID screeningId,
        UUID customerId,
        String movieTitle,
        String status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
