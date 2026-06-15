package com.jbryan98.bookingapp.booking.api.dto;

import com.jbryan98.bookingapp.booking.domain.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID screeningId,
        UUID customerId,
        String movieTitle,
        BookingStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
