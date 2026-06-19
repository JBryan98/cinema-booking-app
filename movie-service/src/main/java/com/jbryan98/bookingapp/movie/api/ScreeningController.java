package com.jbryan98.bookingapp.movie.api;

import com.jbryan98.bookingapp.movie.api.dto.ScreeningRequest;
import com.jbryan98.bookingapp.movie.api.dto.ScreeningResponse;
import com.jbryan98.bookingapp.movie.application.ScreeningService;
import com.jbryan98.bookingapp.movie.chaos.ChaosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/screenings")
@RequiredArgsConstructor
public class ScreeningController {
    private final ScreeningService service;
    private final ChaosService chaosService;

    @GetMapping
    public ResponseEntity<Page<ScreeningResponse>> getScreenings(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreeningResponse> getScreeningById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScreeningResponse> createScreening(@Valid @RequestBody ScreeningRequest request) {
        var response = service.create(request);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}/reserve")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ScreeningResponse> reserveScreening(@PathVariable UUID id) throws InterruptedException {
        chaosService.applyCurrentChaos();
        return ResponseEntity.ok(service.reserve(id));
    }

    @PatchMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ScreeningResponse> releaseScreening(@PathVariable UUID id) {
        return ResponseEntity.ok(service.release(id));
    }
}
