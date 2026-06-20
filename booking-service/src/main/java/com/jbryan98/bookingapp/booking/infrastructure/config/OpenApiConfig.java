package com.jbryan98.bookingapp.booking.infrastructure.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración OpenAPI 3.1 para booking-service.
 *
 * UI: http://localhost:51200/api/v1/swagger-ui.html
 * Spec JSON: http://localhost:51200/api/v1/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Bookings Service API")
                                .description("""
                                Servicio de gestión de reservas del cinema-booking-app MitoCode.

                                **Ciclo de vida de una reserva:**
                                ```
                                POST /bookings        → crea orden (PENDING)
                                PATCH /{id}/confirm   → PENDING → CONFIRMED
                                PATCH /{id}/cancel    → PENDING | CONFIRMED → CANCELLED
                                ```
                                """)
                                .version("7.8.25")
                                .contact(
                                        new Contact()
                                                .name("Bryan Corrales")
                                                .email("bcorrales@demo.com")
                                )
                )
                .addServersItem(new Server()
                        .url("http://localhost:51200/api/v1")
                        .description("Servidor local"))
                .addServersItem(new Server()
                        .url("http://qa.mitocode.com/api/v1")
                        .description("Servidor de QA"))
                .addServersItem(new Server()
                        .url("http://mitocode.com/api/v1")
                        .description("Servidor de Producción"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT RS256 — obtener con POST /api/v1/auth/login en auth-service")));
    }
}
