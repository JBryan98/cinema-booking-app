package com.jbryan98.bookingapp.orchestrator.infrastucture.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public record ServiceUrlProperties(
        ServiceUrl movieService,
        ServiceUrl bookingService
) {
    public record ServiceUrl(String url) {
    }

}