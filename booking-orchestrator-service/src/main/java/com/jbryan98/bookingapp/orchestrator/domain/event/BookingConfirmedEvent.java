package com.jbryan98.bookingapp.orchestrator.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingConfirmedEvent(
        UUID bookingId,
        UUID screeningId,
        UUID customerId,
        String movieTitle,
        BigDecimal totalAmount,
        Instant occurredAt
) {
}
