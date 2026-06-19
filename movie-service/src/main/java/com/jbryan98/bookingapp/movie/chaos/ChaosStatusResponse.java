package com.jbryan98.bookingapp.movie.chaos;

/**
 * Estado actual del modo caos del payment-service.
 *
 * @param mode              Modo activo (NONE, LATENCY, ERROR, ERROR_AFTER)
 * @param latencyMs         Delay activo en milisegundos (0 si no aplica)
 * @param errorRate         Tasa de error activa 0.0-1.0 (0.0 si no aplica)
 * @param failAfterAttempts Intentos que fallarán (0 si no aplica)
 * @param description       Descripción legible del estado actual
 */
public record ChaosStatusResponse(
        String mode,
        long latencyMs,
        double errorRate,
        int failAfterAttempts,
        String description
) {}
