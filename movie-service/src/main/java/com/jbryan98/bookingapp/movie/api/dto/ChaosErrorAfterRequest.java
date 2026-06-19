package com.jbryan98.bookingapp.movie.api.dto;

/**
 * @param attempts Número de intentos iniciales que fallarán antes de responder OK
 */
public record ChaosErrorAfterRequest(int attempts) {}
