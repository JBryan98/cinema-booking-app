package com.jbryan98.bookingapp.booking.domain.repository;

import com.jbryan98.bookingapp.booking.domain.entity.Booking;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(UUID id);
}
