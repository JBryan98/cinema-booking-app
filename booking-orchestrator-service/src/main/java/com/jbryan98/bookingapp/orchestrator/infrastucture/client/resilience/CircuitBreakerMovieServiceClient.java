package com.jbryan98.bookingapp.orchestrator.infrastucture.client.resilience;

import com.jbryan98.bookingapp.orchestrator.exception.ReserveScreeningFailedByRetriesException;
import com.jbryan98.bookingapp.orchestrator.exception.SagaException;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.MovieServiceHttpClient;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.ScreeningResponse;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.httpexchange.MovieServiceHttpExchange;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("circuit-breaker")
public class CircuitBreakerMovieServiceClient implements MovieServiceHttpClient {

    private final MovieServiceHttpExchange exchange;

    @Override
    public ScreeningResponse getScreeningById(UUID screeningId) {
        try {
            log.info("Get screening with id {}", screeningId);
            return exchange.getScreeningById(screeningId);
        } catch (RestClientResponseException ex) {
            throw new SagaException("Error " + ex.getStatusCode() + " al validar la función.", ex);
        }
    }

    /**
     * Integración de Circuit Breaker y Retry
     * Retry → CircuitBreaker → Servicio externo (exchange.reserveScreening)
     * - Retry decide cuantas veces intentar
     * - Circuit Breaker decide si el intento puede pasar
     * - El fallback se ejecuta en el nivel del Retry, cuando ya no hay más intentos posibles
     */
    @Override
    @CircuitBreaker(name = "reserveScreeningCircuitBreaker")
    @Retry(name = "reserveScreeningRetry", fallbackMethod = "reserveScreeningFallback")
    public ScreeningResponse reserveScreening(UUID screeningId) {
        log.info("[RETRY] Calling reserveScreening — screeningId: {}", screeningId);
        return exchange.reserveScreening(screeningId);
    }

    private ScreeningResponse reserveScreeningFallback(UUID screeningId, Throwable ex) {
        // Si la excepción es CallNotPermittedException, significa que el Circuit Breaker está abierto.
        if (ex instanceof CallNotPermittedException) {
            log.error("[CB_OPEN] Circuit Breaker OPEN — movie-service no disponible ({})", ex.getMessage());
            throw new SagaException("Movie service no disponible, intente nuevamente más tarde.", ex);
        }
        log.error("[RETRY] All retry attempts failed for screeningId {}. Error: {}", screeningId, ex.toString(), ex);
        throw new ReserveScreeningFailedByRetriesException("Reserva fallida tras reintentos - screeningId " + screeningId, ex);
    }

    @Override
    public ScreeningResponse releaseScreening(UUID screeningId) {
        try {
            log.info("Release screening with id {}", screeningId);
            return exchange.releaseScreening(screeningId);
        } catch (RestClientResponseException ex) {
            throw new SagaException("Error " + ex.getStatusCode() + " al liberar la función.", ex);
        }
    }
}