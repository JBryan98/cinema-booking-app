package com.jbryan98.bookingapp.movie.domain.entity;

import com.jbryan98.bookingapp.movie.exception.AvailableSeatsExceedsTotalSeatsException;
import com.jbryan98.bookingapp.movie.exception.InsufficientSeatsException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(name = "screenings")
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false, length = 50)
    private String room;

    @Column(nullable = false)
    private LocalDateTime showAt;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private boolean active = true;

    public Screening() {}

    public static Screening create(String room, Movie movie, LocalDateTime showAt, Integer totalSeats, Integer availableSeats) {
        Screening screening = new Screening();
        screening.room = room;
        screening.movie = movie;
        screening.showAt = showAt;
        if (availableSeats > totalSeats) {
            throw new AvailableSeatsExceedsTotalSeatsException("Available seats cannot exceed total seats");
        }
        screening.totalSeats = totalSeats;
        screening.availableSeats = availableSeats;
        return screening;
    }

    public void reserve() {
        if (availableSeats <= 0) {
            throw new InsufficientSeatsException("No available seats");
        }
        this.availableSeats--;
    }

    public void release() {
        if (!Objects.equals(availableSeats, totalSeats)) {
            this.availableSeats++;
        }
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
