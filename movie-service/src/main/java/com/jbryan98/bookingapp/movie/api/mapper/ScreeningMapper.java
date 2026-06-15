package com.jbryan98.bookingapp.movie.api.mapper;

import com.jbryan98.bookingapp.movie.api.dto.ScreeningResponse;
import com.jbryan98.bookingapp.movie.domain.entity.Screening;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScreeningMapper {
    private final MovieMapper movieMapper;

    public ScreeningResponse toResponse(Screening screening) {
        return new ScreeningResponse(
                screening.getId(),
                movieMapper.toResponse(screening.getMovie()),
                screening.getRoom(),
                screening.getShowAt(),
                screening.getTotalSeats(),
                screening.getAvailableSeats(),
                screening.isActive()
        );
    }
}
