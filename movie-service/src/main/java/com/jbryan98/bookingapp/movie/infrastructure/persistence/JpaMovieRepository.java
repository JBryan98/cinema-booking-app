package com.jbryan98.bookingapp.movie.infrastructure.persistence;

import com.jbryan98.bookingapp.movie.domain.entity.Movie;
import com.jbryan98.bookingapp.movie.domain.repository.MovieRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaMovieRepository extends JpaRepository<Movie, UUID>, MovieRepository {
}
