package com.jbryan98.bookingapp.orchestrator.infrastucture.client.httpexchange;

import com.jbryan98.bookingapp.orchestrator.exception.SagaException;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.BookingServiceHttpClient;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.BookingRequest;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.BookingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HttpExchangeBookingServiceClient implements BookingServiceHttpClient {
    private final BookingServiceHttpExchange exchange;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        try {
            log.info("Create booking {}", request);
            return exchange.createBooking(request);
        } catch (RestClientResponseException ex) {
            throw new SagaException("Error " + ex.getStatusCode() + " al crear la reserva.", ex);
        }
    }

    @Override
    public BookingResponse confirmBooking(UUID bookingId) {
        try {
            log.info("Confirm booking with id {}", bookingId);
            return exchange.confirmBooking(bookingId);
        } catch (RestClientResponseException ex) {
            throw new SagaException("Error " + ex.getStatusCode() + " al confirmar la reserva.", ex);
        }
    }

    @Override
    public BookingResponse cancelBooking(UUID bookingId) {
        try {
            log.info("Cancel booking with id {}", bookingId);
            return exchange.cancelBooking(bookingId);
        } catch (RestClientResponseException ex) {
            throw new SagaException("Error " + ex.getStatusCode() + " al cancelar la reserva.", ex);
        }
    }
}
