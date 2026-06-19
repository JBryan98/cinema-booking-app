package com.jbryan98.bookingapp.movie.chaos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChaosService {

    private final ChaosState chaosState;

    public ChaosService(ChaosState chaosState) {
        this.chaosState = chaosState;
    }

    public void applyCurrentChaos() throws InterruptedException {
        switch (chaosState.getMode()) {

            case LATENCY -> {
                log.warn("[CHAOS] Applying latency: {}ms", chaosState.getLatencyMs());
                Thread.sleep(chaosState.getLatencyMs());
            }

            case ERROR -> {
                if (Math.random() < chaosState.getErrorRate()) {
                    log.warn("[CHAOS] Simulating random error (rate={})", chaosState.getErrorRate());

                    throw new RuntimeException("Movie service error: service unavailable (simulated)");
                }
            }

            case ERROR_AFTER -> {
                if (chaosState.shouldFailThisAttempt()) {
                    log.warn("[CHAOS] Simulating error (attempt will fail, max={})",
                            chaosState.getFailAfterAttempts());
                    throw new RuntimeException("Movie service error: temporary failure (simulated)");
                }
                log.info("[CHAOS] Attempt passed, no more errors");
            }

            case NONE -> { /* comportamiento normal */ }

        }
    }

    public void enableLatency(long ms) {
        log.warn("[CHAOS] Enabling latency mode: {}ms", ms);
        chaosState.enableLatency(ms);
    }

    public void enableError(double rate) {
        log.warn("[CHAOS] Enabling error mode: {}% failure rate", (int) (rate * 100));
        chaosState.enableError(rate);
    }

    public void enableErrorAfter(int attempts) {
        log.warn("[CHAOS] Enabling error-after mode: first {} attempts will fail", attempts);
        chaosState.enableErrorAfter(attempts);
    }

    public void reset() {
        log.warn("[CHAOS] Resetting chaos mode to NONE");
        chaosState.reset();
    }

    public ChaosStatusResponse getStatus() {
        String description = switch (chaosState.getMode()) {
            case NONE -> "Normal behavior — no chaos active";
            case LATENCY -> "Adding %dms delay to every response".formatted(chaosState.getLatencyMs());
            case ERROR -> "%d%% of requests will fail with 500".formatted((int) (chaosState.getErrorRate() * 100));
            case ERROR_AFTER -> "First %d attempts fail, then normal".formatted(chaosState.getFailAfterAttempts());
        };

        return new ChaosStatusResponse(
                chaosState.getMode().name(),
                chaosState.getLatencyMs(),
                chaosState.getErrorRate(),
                chaosState.getFailAfterAttempts(),
                description
        );
    }

}
