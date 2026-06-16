package com.jbryan98.bookingapp.orchestrator.domain.event;

import java.time.Instant;
import java.util.UUID;

public record BookingFailedEvent(
        UUID customerId,
        String failedStep,
        String reason,
        Instant occurredAt
) {
}
