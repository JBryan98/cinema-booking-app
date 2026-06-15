package com.jbryan98.bookingapp.movie.infrastructure.persistence;

import com.jbryan98.bookingapp.movie.domain.entity.Screening;
import com.jbryan98.bookingapp.movie.domain.repository.ScreeningRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaScreeningRepository extends JpaRepository<Screening, UUID>, ScreeningRepository {
}
