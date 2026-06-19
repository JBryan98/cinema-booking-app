package com.jbryan98.bookingapp.movie.api.dto;

/**
 * @param rate Tasa de error entre 0.0 y 1.0 (ej: 0.8 = 80% de las llamadas fallan)
 */
public record ChaosErrorRequest(double rate) {}
