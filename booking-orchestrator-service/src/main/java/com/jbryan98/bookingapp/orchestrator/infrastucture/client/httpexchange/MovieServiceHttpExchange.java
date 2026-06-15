package com.jbryan98.bookingapp.orchestrator.infrastucture.client.httpexchange;

import com.jbryan98.bookingapp.orchestrator.infrastucture.client.dto.ScreeningResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;

import java.util.UUID;

@HttpExchange
public interface MovieServiceHttpExchange {
    @GetExchange("/screenings/{id}")
    ScreeningResponse getScreeningById(@PathVariable(name = "id") UUID screeningId);

    @PatchExchange("/screenings/{id}/reserve")
    ScreeningResponse reserveScreening(@PathVariable(name = "id") UUID screeningId);

    @PatchExchange("/screenings/{id}/release")
    ScreeningResponse releaseScreening(@PathVariable(name = "id") UUID screeningId);
}
