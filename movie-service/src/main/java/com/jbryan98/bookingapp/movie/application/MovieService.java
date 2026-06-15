package com.jbryan98.bookingapp.movie.application;

import com.jbryan98.bookingapp.movie.api.dto.MovieRequest;
import com.jbryan98.bookingapp.movie.api.dto.MovieResponse;
import com.jbryan98.bookingapp.movie.domain.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MovieService {
    Page<MovieResponse> findAll(Pageable pageable);

    Movie findEntityById(UUID id);

    MovieResponse findById(UUID id);

    MovieResponse create(MovieRequest request);

    MovieResponse update(UUID id, MovieRequest request);

    void delete(UUID id);

    MovieResponse activate(UUID id);

    MovieResponse deactivate(UUID id);
}
