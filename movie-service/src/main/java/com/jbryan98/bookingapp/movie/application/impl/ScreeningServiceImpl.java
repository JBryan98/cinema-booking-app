package com.jbryan98.bookingapp.movie.application.impl;

import com.jbryan98.bookingapp.movie.api.dto.ScreeningRequest;
import com.jbryan98.bookingapp.movie.api.dto.ScreeningResponse;
import com.jbryan98.bookingapp.movie.api.mapper.ScreeningMapper;
import com.jbryan98.bookingapp.movie.application.MovieService;
import com.jbryan98.bookingapp.movie.application.ScreeningService;
import com.jbryan98.bookingapp.movie.domain.entity.Screening;
import com.jbryan98.bookingapp.movie.domain.repository.ScreeningRepository;
import com.jbryan98.bookingapp.movie.exception.ScreeningNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ScreeningServiceImpl implements ScreeningService {
    private final ScreeningRepository repository;
    private final MovieService movieService;
    private final ScreeningMapper mapper;


    @Override
    public Page<ScreeningResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public ScreeningResponse findById(UUID id) {
        log.info("findById id {}", id);
        var screening = repository.findById(id).orElseThrow(() -> new ScreeningNotFoundException(id));
        return mapper.toResponse(screening);
    }

    @Override
    public ScreeningResponse create(ScreeningRequest request) {
        var movie = movieService.findEntityById(request.movieId());
        var screening = Screening.create(
                request.room(),
                movie,
                request.showAt(),
                request.totalSeats(),
                request.availableSeats()
        );
        repository.save(screening);
        return mapper.toResponse(screening);
    }

    @Override
    public ScreeningResponse reserve(UUID id) {
        log.info("reserve id {}", id);
        var screening = repository.findById(id).orElseThrow(() -> new ScreeningNotFoundException(id));
        screening.reserve();
        repository.save(screening);
        return mapper.toResponse(screening);
    }

    @Override
    public ScreeningResponse release(UUID id) {
        log.info("release id {}", id);
        var screening = repository.findById(id).orElseThrow(() -> new ScreeningNotFoundException(id));
        screening.release();
        repository.save(screening);
        return mapper.toResponse(screening);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ScreeningNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    public ScreeningResponse activate(UUID id) {
        var screening = repository.findById(id).orElseThrow(() -> new ScreeningNotFoundException(id));
        screening.activate();
        return mapper.toResponse(screening);
    }

    @Override
    public ScreeningResponse deactivate(UUID id) {
        var screening = repository.findById(id).orElseThrow(() -> new ScreeningNotFoundException(id));
        screening.deactivate();
        return mapper.toResponse(screening);
    }

}
