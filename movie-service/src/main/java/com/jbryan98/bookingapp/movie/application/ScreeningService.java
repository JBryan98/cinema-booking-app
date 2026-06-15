package com.jbryan98.bookingapp.movie.application;

import com.jbryan98.bookingapp.movie.api.dto.ScreeningRequest;
import com.jbryan98.bookingapp.movie.api.dto.ScreeningResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ScreeningService {
    Page<ScreeningResponse> findAll(Pageable pageable);
    
    ScreeningResponse findById(UUID id);

    ScreeningResponse create(ScreeningRequest request);

    ScreeningResponse reserve(UUID id);

    ScreeningResponse release(UUID id);

    void delete(UUID id);

    ScreeningResponse activate(UUID id);

    ScreeningResponse deactivate(UUID id);
}
