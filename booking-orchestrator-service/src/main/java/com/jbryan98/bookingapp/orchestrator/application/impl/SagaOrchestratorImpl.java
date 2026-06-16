package com.jbryan98.bookingapp.orchestrator.application.impl;

import com.jbryan98.bookingapp.orchestrator.api.dto.BookingPlacementResponse;
import com.jbryan98.bookingapp.orchestrator.api.dto.PlaceBookingRequest;
import com.jbryan98.bookingapp.orchestrator.application.EventPublisher;
import com.jbryan98.bookingapp.orchestrator.application.SagaOrchestrator;
import com.jbryan98.bookingapp.orchestrator.domain.event.BookingConfirmedEvent;
import com.jbryan98.bookingapp.orchestrator.domain.event.BookingFailedEvent;
import com.jbryan98.bookingapp.orchestrator.exception.InsufficientSeatsException;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.BookingServiceHttpClient;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.MovieServiceHttpClient;
import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.BookingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestratorImpl implements SagaOrchestrator {

    private final MovieServiceHttpClient movieServiceClient;
    private final BookingServiceHttpClient bookingServiceClient;
    private final EventPublisher eventPublisher;

    @Override
    public BookingPlacementResponse placeBooking(PlaceBookingRequest request) {
        log.info("placeBooking {}", request);
        String currentStep = "unknown";
        UUID bookingId = null;
        final UUID screeningId = request.screeningId();
        boolean seatReserved = false;
        try {
            //----------------------------------------------------------------
            // Paso 1: Validar si la función tiene asientos disponibles
            currentStep = "validate_screening";
            var screening = movieServiceClient.getScreeningById(screeningId);
            if (!(screening.availableSeats() > 0)) {
                throw new InsufficientSeatsException("La función no tiene asientos disponibles.");
            }
            log.info("SAGA [1/4] Función validada");
            //----------------------------------------------------------------

            //----------------------------------------------------------------
            // Paso 2: Crear reserva con estado PENDING
            currentStep = "create_booking";
            var bookingRq = new BookingRequest(
                    screeningId,
                    request.customerId(),
                    screening.movie().title(),
                    request.totalAmount()
            );
            var booking = bookingServiceClient.createBooking(bookingRq);
            bookingId = booking.id();
            log.info("SAGA [2/4] Reserva creada con id {}", bookingId);
            //----------------------------------------------------------------

            //----------------------------------------------------------------
            // Paso 3: Reservar asiento en la función
            currentStep = "reserve_seat";
            movieServiceClient.reserveScreening(screeningId);
            seatReserved = true;
            log.info("SAGA [3/4] Asiento reservado en la función con id {}", screeningId);
            //----------------------------------------------------------------

            // Paso 4: Confirmar reserva
            currentStep = "confirm_booking";
            var confirmedBooking = bookingServiceClient.confirmBooking(bookingId);
            log.info("SAGA [4/4] Reserva confirmada con id {}", bookingId);
            //----------------------------------------------------------------

            //----------------------------------------------------------------
            // Evento Kafka: notificación asíncrona
            eventPublisher.publishBookingConfirmed(
                    new BookingConfirmedEvent(
                            confirmedBooking.id(),
                            confirmedBooking.screeningId(),
                            confirmedBooking.customerId(),
                            confirmedBooking.movieTitle(),
                            confirmedBooking.totalAmount(),
                            Instant.now()
                    )
            );
            //----------------------------------------------------------------


            return new BookingPlacementResponse(
                    confirmedBooking.id(),
                    confirmedBooking.screeningId(),
                    confirmedBooking.customerId(),
                    confirmedBooking.movieTitle(),
                    confirmedBooking.status(),
                    confirmedBooking.totalAmount(),
                    confirmedBooking.createdAt()
            );
        } catch (Exception ex) {
            log.error("SAGA fallida en el paso '{}': {}", currentStep, ex.getMessage(), ex);
            final UUID failureBookingId = bookingId;
            if (seatReserved) {
                compensate("release_screening", () -> movieServiceClient.releaseScreening(screeningId));
            }
            if (bookingId != null) {
                compensate("cancel_booking", () -> bookingServiceClient.cancelBooking(failureBookingId));
            }

            eventPublisher.publishBookingFailed(
                    new BookingFailedEvent(
                            request.customerId(),
                            currentStep,
                            ex.getMessage(),
                            Instant.now()
                    )
            );

            throw ex;
        }
    }

    @Override
    public BookingPlacementResponse cancelBooking(UUID bookingId) {
        log.info("cancelBooking {}", bookingId);
        String currentStep = "unknown";

        try {
            //----------------------------------------------------------------
            // Paso 1: Validar que la reserva existe y puede cancelarse
            currentStep = "cancel_booking";
            var booking = bookingServiceClient.cancelBooking(bookingId);
            log.info("SAGA [1/1] La reserva fue cancelada exitosamente con id {}", bookingId);
            //----------------------------------------------------------------

            //----------------------------------------------------------------
            // Paso 2: Liberar el asiento reservado en la función
            currentStep = "release_screening";
            movieServiceClient.releaseScreening(booking.screeningId());
            log.info("SAGA [2/2] Asiento liberado en la función con id {}", booking.screeningId());
            //----------------------------------------------------------------

            return new BookingPlacementResponse(
                    booking.id(),
                    booking.screeningId(),
                    booking.customerId(),
                    booking.movieTitle(),
                    booking.status(),
                    booking.totalAmount(),
                    booking.createdAt()
            );
        } catch (Exception ex) {
            log.error("SAGA fallida en el paso '{}': {}", currentStep, ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Ejecuta una acción de compensación absorbiendo cualquier excepción.
     * Los fallos de compensación no deben enmascarar el error original de la SAGA.
     * Si una compensación falla queda registrada en logs para intervención manual.
     */
    private void compensate(String step, Runnable action) {
        try {
            log.info("SAGA compensación: ejecutando '{}'", step);
            action.run();
            log.info("SAGA compensación '{}' completada", step);
        } catch (Exception ex) {
            log.error("SAGA compensación '{}' falló — requiere intervención manual: {}", step, ex.getMessage());
        }

    }
}
