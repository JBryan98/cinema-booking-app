package com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto;

import java.util.UUID;

public record MovieResponse(
        UUID id,
        String title,
        String director,
        String genre,
        Integer durationMinutes,
        String rating,
        boolean active
) {
}