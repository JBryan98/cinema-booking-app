package com.jbryan98.bookingapp.movie.api.dto;

/**
 * @param ms Milisegundos de latencia artificial a inyectar en cada respuesta
 */
public record ChaosLatencyRequest(long ms) {}
