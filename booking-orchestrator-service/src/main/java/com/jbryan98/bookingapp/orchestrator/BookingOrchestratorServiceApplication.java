package com.jbryan98.bookingapp.orchestrator;

import com.jbryan98.bookingapp.orchestrator.infrastucture.config.ServiceUrlProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlProperties.class)
public class BookingOrchestratorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingOrchestratorServiceApplication.class, args);
    }

}
