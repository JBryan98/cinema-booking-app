package com.jbryan98.bookingapp.booking.infrastructure.persistence;

import com.jbryan98.bookingapp.booking.domain.entity.Booking;
import com.jbryan98.bookingapp.booking.domain.repository.BookingRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaBookingRepository extends JpaRepository<Booking, UUID>, BookingRepository {
}
