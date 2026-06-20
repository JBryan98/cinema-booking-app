package com.jbryan98.bookingapp.movie.api;

import com.jbryan98.bookingapp.movie.api.dto.ChaosErrorAfterRequest;
import com.jbryan98.bookingapp.movie.api.dto.ChaosErrorRequest;
import com.jbryan98.bookingapp.movie.api.dto.ChaosLatencyRequest;
import com.jbryan98.bookingapp.movie.chaos.ChaosService;
import com.jbryan98.bookingapp.movie.chaos.ChaosStatusResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chaos")
@Hidden // Oculta esta API de la documentación pública, es solo para demos internas
@PreAuthorize("hasRole('ADMIN')") // Solo usuarios con rol ADMIN pueden activar el modo caos
public class ChaosController {

    private final ChaosService chaosService;

    public ChaosController(ChaosService chaosService) {
        this.chaosService = chaosService;
     }

    /**
     * Consulta el estado actual del modo caos.
     * Usar antes de una demo para confirmar que el servicio está en modo normal.
     */
    @GetMapping
    public ResponseEntity<ChaosStatusResponse> status() {
        return ResponseEntity.ok(chaosService.getStatus());
    }

    /**
     * Activa latencia artificial en todas las respuestas.
     *
     * Uso en demo TimeLimiter / CircuitBreaker (slowCallDuration):
     *   PUT /chaos/latency  {"ms": 5000}
     *   → El timeout del TimeLimiter se activa y corta la llamada
     */
    @PutMapping("/latency")
    public ResponseEntity<ChaosStatusResponse> enableLatency(@RequestBody ChaosLatencyRequest request) {
        chaosService.enableLatency(request.ms());
        return ResponseEntity.ok(chaosService.getStatus());
    }

    /**
     * Activa errores aleatorios según una tasa configurable.
     *
     * Uso en demo CircuitBreaker (por error rate):
     *   PUT /chaos/error  {"rate": 0.8}
     *   → 80% de las llamadas fallan → el CB detecta la tasa y se abre
     */
    @PutMapping("/error")
    public ResponseEntity<ChaosStatusResponse> enableError(@RequestBody ChaosErrorRequest request) {
        chaosService.enableError(request.rate());
        return ResponseEntity.ok(chaosService.getStatus());
    }

    /**
     * Activa fallos en los primeros N intentos, luego vuelve a funcionar.
     *
     * Uso en demo Retry:
     *   PUT /chaos/error-after  {"attempts": 2}
     *   → Los intentos 1 y 2 fallan → el intento 3 funciona
     *   → El Retry del orquestador hace el reintento automáticamente → éxito
     */
    @PutMapping("/error-after")
    public ResponseEntity<ChaosStatusResponse> enableErrorAfter(@RequestBody ChaosErrorAfterRequest request) {
        chaosService.enableErrorAfter(request.attempts());
        return ResponseEntity.ok(chaosService.getStatus());
    }

    /**
     * Resetea el modo caos — vuelve al comportamiento normal.
     * Usar al finalizar la demo para dejar el servicio limpio.
     */
    @DeleteMapping
    public ResponseEntity<ChaosStatusResponse> reset() {
        chaosService.reset();
        return ResponseEntity.ok(chaosService.getStatus());
    }
}
