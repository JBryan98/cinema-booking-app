package com.jbryan98.bookingapp.movie.application.impl;

import com.jbryan98.bookingapp.movie.api.dto.MovieRequest;
import com.jbryan98.bookingapp.movie.api.dto.MovieResponse;
import com.jbryan98.bookingapp.movie.api.mapper.MovieMapper;
import com.jbryan98.bookingapp.movie.application.MovieService;
import com.jbryan98.bookingapp.movie.domain.entity.Movie;
import com.jbryan98.bookingapp.movie.domain.repository.MovieRepository;
import com.jbryan98.bookingapp.movie.exception.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository repository;
    private final MovieMapper mapper;

    @Override
    public Page<MovieResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public Movie findEntityById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
    }

    @Override
    public MovieResponse findById(UUID id) {
        var movie = findEntityById(id);
        return mapper.toResponse(movie);
    }

    @Override
    public MovieResponse create(MovieRequest request) {
        var movie = Movie.create(
                request.title(),
                request.director(),
                request.genre(),
                request.durationMinutes(),
                request.rating()
        );
        return mapper.toResponse(repository.save(movie));
    }

    @Override
    public MovieResponse update(UUID id, MovieRequest request) {
        var movie = findEntityById(id);
        movie.setTitle(request.title());
        movie.setDirector(request.director());
        movie.setGenre(request.genre());
        movie.setDurationMinutes(request.durationMinutes());
        movie.setRating(request.rating());
        return mapper.toResponse(repository.save(movie));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new MovieNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    public MovieResponse activate(UUID id) {
        var movie = findEntityById(id);
        movie.activate();
        return mapper.toResponse(repository.save(movie));
    }

    @Override
    public MovieResponse deactivate(UUID id) {
        var movie = findEntityById(id);
        movie.deactivate();
        return mapper.toResponse(repository.save(movie));
    }
}
