package com.jbryan98.bookingapp.movie.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScreeningResponse(
        UUID id,
        MovieResponse movie,
        String room,
        LocalDateTime showAt,
        Integer totalSeats,
        Integer availableSeats,
        Boolean active
) {
}
