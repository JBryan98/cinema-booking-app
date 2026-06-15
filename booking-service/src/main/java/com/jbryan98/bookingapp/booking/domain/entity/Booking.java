package com.jbryan98.bookingapp.booking.domain.entity;

import com.jbryan98.bookingapp.booking.exception.BookingAlreadyCancelledException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "bookings")
@EntityListeners(AuditingEntityListener.class)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID screeningId;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private String movieTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    public Booking() {
    }

    public static Booking create(UUID screeningId,  UUID customerId, String movieTitle, BigDecimal totalAmount) {
        var booking = new Booking();
        booking.screeningId = screeningId;
        booking.customerId = customerId;
        booking.movieTitle = movieTitle;
        booking.status = BookingStatus.PENDING;
        booking.totalAmount = totalAmount;
        return booking;
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (status.equals(BookingStatus.CANCELLED)) {
            throw new BookingAlreadyCancelledException("Booking with id " + id + " is already cancelled");
        }
        this.status = BookingStatus.CANCELLED;
    }
}
