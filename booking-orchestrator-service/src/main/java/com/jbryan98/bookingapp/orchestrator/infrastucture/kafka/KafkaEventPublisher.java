package com.jbryan98.bookingapp.orchestrator.infrastucture.kafka;

import com.jbryan98.bookingapp.orchestrator.application.EventPublisher;
import com.jbryan98.bookingapp.orchestrator.domain.event.BookingConfirmedEvent;
import com.jbryan98.bookingapp.orchestrator.domain.event.BookingFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher implements EventPublisher {
    static final String TOPIC_CINEMA_BOOKINGS = "cinema.bookings";
    static final String TOPIC_BOOKINGS_FAILED = "bookings.failed";

    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        kafkaTemplate.send(TOPIC_CINEMA_BOOKINGS, event.bookingId().toString(), event);
        log.info("Evento publicado topic: {} , bookingId: {}", TOPIC_CINEMA_BOOKINGS, event.bookingId());
    }

    @Override
    public void publishBookingFailed(BookingFailedEvent event) {
        kafkaTemplate.send(TOPIC_BOOKINGS_FAILED, event.customerId().toString(), event);
        log.warn("Evento publicado topic: {} , failedStep: {}", TOPIC_BOOKINGS_FAILED, event.failedStep());
    }
}
