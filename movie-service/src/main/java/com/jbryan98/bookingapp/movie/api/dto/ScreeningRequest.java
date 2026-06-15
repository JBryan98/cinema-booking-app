package com.jbryan98.bookingapp.movie.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScreeningRequest(
        @NotNull(message = "movieId is required.")
        UUID movieId,

        @NotBlank(message = "room is required.")
        String room,

        @NotNull(message = "showAt is required.")
        @FutureOrPresent(message = "showAt must be in the present or future.")
        LocalDateTime showAt,

        @NotNull(message = "totalSeats is required.")
        @Positive(message = "totalSeats must be a positive integer.")
        Integer totalSeats,

        @NotNull(message = "availableSeats is required.")
        @Positive(message = "availableSeats must be a positive integer.")
        Integer availableSeats
) {
}
