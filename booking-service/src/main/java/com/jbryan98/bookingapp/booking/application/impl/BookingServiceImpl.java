package com.jbryan98.bookingapp.booking.application.impl;

import com.jbryan98.bookingapp.booking.api.dto.BookingRequest;
import com.jbryan98.bookingapp.booking.api.dto.BookingResponse;
import com.jbryan98.bookingapp.booking.application.BookingService;
import com.jbryan98.bookingapp.booking.domain.entity.Booking;
import com.jbryan98.bookingapp.booking.domain.repository.BookingRepository;
import com.jbryan98.bookingapp.booking.exception.BookingNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;

    @Override
    public BookingResponse create(BookingRequest request) {
        log.info("create {}", request);
        var booking = Booking.create(request.screeningId(), request.customerId(), request.movieTitle(), request.totalAmount());
        repository.save(booking);
        return toResponse(booking);
    }

    @Override
    public BookingResponse findById(UUID id) {
        log.info("findById id {}", id);
        var booking = repository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
        return toResponse(booking);
    }

    @Override
    public BookingResponse confirm(UUID id) {
        log.info("confirm {}", id);
        var booking = repository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
        booking.confirm();
        repository.save(booking);
        return toResponse(booking);
    }

    @Override
    public BookingResponse cancel(UUID id) {
        log.info("cancel {}", id);
        var booking = repository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
        booking.cancel();
        repository.save(booking);
        return toResponse(booking);
    }


    private BookingResponse toResponse(Booking booking) {
        var zone = ZoneId.systemDefault();
        return new BookingResponse(
                booking.getId(),
                booking.getScreeningId(),
                booking.getCustomerId(),
                booking.getMovieTitle(),
                booking.getStatus(),
                booking.getTotalAmount(),
                LocalDateTime.ofInstant(booking.getCreatedAt(), zone)
        );
    }
}
