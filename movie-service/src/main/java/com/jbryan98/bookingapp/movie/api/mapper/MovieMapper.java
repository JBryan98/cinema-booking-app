package com.jbryan98.bookingapp.movie.api.mapper;

import com.jbryan98.bookingapp.movie.api.dto.MovieResponse;
import com.jbryan98.bookingapp.movie.domain.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {
    public MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getGenre(),
                movie.getDurationMinutes(),
                movie.getRating(),
                movie.isActive()
        );
    }
}
