package com.jbryan98.bookingapp.orchestrator.infrastucture.client;

import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.ScreeningResponse;

import java.util.UUID;

public interface MovieServiceHttpClient {
    ScreeningResponse getScreeningById(UUID screeningId);

    ScreeningResponse reserveScreening(UUID screeningId);

    ScreeningResponse releaseScreening(UUID screeningId);
}