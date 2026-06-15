package com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID screeningId,
        UUID customerId,
        String movieTitle,
        String status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
