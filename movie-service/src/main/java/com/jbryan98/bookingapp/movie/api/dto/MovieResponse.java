package com.jbryan98.bookingapp.movie.api.dto;

import com.jbryan98.bookingapp.movie.domain.entity.Genre;

import java.util.UUID;

public record MovieResponse(
        UUID id,
        String title,
        String director,
        Genre genre,
        Integer durationMinutes,
        String rating,
        boolean active
) {
}