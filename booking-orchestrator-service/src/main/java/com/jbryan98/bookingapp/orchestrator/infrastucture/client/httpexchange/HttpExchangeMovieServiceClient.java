package com.jbryan98.bookingapp.orchestrator.infrastucture.client.httpexchange;

import com.jbryan98.bookingapp.orchestrator.exception.SagaException;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.MovieServiceHttpClient;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.ScreeningResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpExchangeMovieServiceClient implements MovieServiceHttpClient {
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

    @Override
    public ScreeningResponse reserveScreening(UUID screeningId) {
        try {
            log.info("Reserve screening with id {}", screeningId);
            return exchange.reserveScreening(screeningId);
        } catch (RestClientResponseException ex) {
            throw new SagaException("Error " + ex.getStatusCode() + " al reservar la función.", ex);
        }
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
