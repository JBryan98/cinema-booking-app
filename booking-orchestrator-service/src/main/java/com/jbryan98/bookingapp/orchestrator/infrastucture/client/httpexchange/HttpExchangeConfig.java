package com.jbryan98.bookingapp.orchestrator.infrastucture.client.httpexchange;

import com.jbryan98.bookingapp.orchestrator.infrastucture.config.ServiceUrlProperties;
import com.jbryan98.bookingapp.orchestrator.security.common.JwtPropagationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "movie-service", types = MovieServiceHttpExchange.class)
@ImportHttpServices(group = "booking-service", types = BookingServiceHttpExchange.class)
public class HttpExchangeConfig {

    @Bean
    RestClientHttpServiceGroupConfigurer groupConfigurer(ServiceUrlProperties properties, JwtPropagationInterceptor interceptor) {
        return groups -> {
            groups.filterByName("movie-service")
                    .forEachClient((group, builder) -> builder
                            .baseUrl(properties.movieService().url())
                            .requestInterceptor(interceptor)
                            .build());

            groups.filterByName("booking-service")
                    .forEachClient((group, builder) -> builder
                            .baseUrl(properties.bookingService().url())
                            .requestInterceptor(interceptor)
                            .build());
        };
    }
}
