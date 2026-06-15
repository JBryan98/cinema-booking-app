package com.jbryan98.bookingapp.movie.api.dto;

import com.jbryan98.bookingapp.movie.domain.entity.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovieRequest(
        @NotBlank(message = "title is required.")
        String title,

        @NotBlank(message = "director is required.")
        String director,

        @NotNull(message = "genre is required.")
        Genre genre,

        @NotNull(message = "durationMinutes is required.")
        @Positive(message = "durationMinutes must be a positive integer.")
        Integer durationMinutes,

        @NotBlank(message = "rating is required.")
        String rating
) {
}
