package com.jbryan98.bookingapp.orchestrator.application;

import com.jbryan98.bookingapp.orchestrator.domain.event.BookingConfirmedEvent;
import com.jbryan98.bookingapp.orchestrator.domain.event.BookingFailedEvent;

public interface EventPublisher {
    void publishBookingConfirmed(BookingConfirmedEvent event);

    void publishBookingFailed(BookingFailedEvent event);
}
