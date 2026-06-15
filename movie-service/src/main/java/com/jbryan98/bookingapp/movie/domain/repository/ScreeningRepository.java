package com.jbryan98.bookingapp.movie.domain.repository;

import com.jbryan98.bookingapp.movie.domain.entity.Screening;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ScreeningRepository {
    Page<Screening> findAll(Pageable pageable);

    Optional<Screening> findById(UUID id);

    Screening save(Screening screening);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
