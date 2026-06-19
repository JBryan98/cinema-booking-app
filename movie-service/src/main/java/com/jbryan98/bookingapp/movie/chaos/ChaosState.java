package com.jbryan98.bookingapp.movie.chaos;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Estado de caos del movie-service — modificable en tiempo de ejecución.
 *
 * Permite simular distintos tipos de fallo en el servicio de pagos sin
 * necesidad de detenerlo ni desplegar una nueva versión.
 *
 * Modos disponibles:
 *   NONE         → comportamiento normal
 *   LATENCY      → agrega un delay configurable a cada respuesta
 *   ERROR        → retorna error en un porcentaje de las llamadas
 *   ERROR_AFTER  → retorna error en los primeros N intentos, luego funciona
 *
 * Útil para demostrar en clase cómo los patrones de resiliencia del
 * order-orchestrator-service reaccionan ante fallos reales del downstream.
 */
@Component
public class ChaosState {

    public enum Mode {
        NONE, LATENCY, ERROR, ERROR_AFTER
    }

    // Modo actual de caos — AtomicReference para thread-safety
    private final AtomicReference<Mode> currentMode = new AtomicReference<>(Mode.NONE);

    // Delay en milisegundos para el modo LATENCY
    private volatile long latencyMs = 0;

    // Tasa de error (0.0 a 1.0) para el modo ERROR
    // Ejemplo: 0.5 = 50% de las llamadas fallan
    private volatile double errorRate = 0.0;

    // Número de intentos que fallarán antes de volver a funcionar (modo ERROR_AFTER)
    private volatile int failAfterAttempts = 0;

    // Contador interno de intentos para el modo ERROR_AFTER
    private final AtomicInteger attemptCounter = new AtomicInteger(0);

    public void enableLatency(long ms) {
        this.latencyMs = ms;
        this.currentMode.set(Mode.LATENCY);
    }

    public void enableError(double rate) {
        this.errorRate = Math.max(0.0, Math.min(1.0, rate)); // clamp entre 0 y 1
        this.currentMode.set(Mode.ERROR);
    }

    public void enableErrorAfter(int attempts) {
        this.failAfterAttempts = attempts;
        this.attemptCounter.set(0);
        this.currentMode.set(Mode.ERROR_AFTER);
    }

    public void reset() {
        this.currentMode.set(Mode.NONE);
        this.latencyMs = 0;
        this.errorRate = 0.0;
        this.failAfterAttempts = 0;
        this.attemptCounter.set(0);
    }

    public Mode getMode() {
        return currentMode.get();
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public int getFailAfterAttempts() {
        return failAfterAttempts;
    }

    /**
     * Para el modo ERROR_AFTER: verifica si este intento debe fallar.
     * Incrementa el contador y falla si aún no se llegó al límite.
     */
    public boolean shouldFailThisAttempt() {
        int attempt = attemptCounter.incrementAndGet();
        return attempt <= failAfterAttempts;
    }
}
