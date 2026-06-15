package com.jbryan98.bookingapp.movie.domain.repository;

import com.jbryan98.bookingapp.movie.domain.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository {
    Movie save(Movie movie);

    Optional<Movie> findById(UUID id);

    Page<Movie> findAll(Pageable pageable);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
