package com.jbryan98.bookingapp.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración OpenAPI 3.1 para auth-service.
 *
 * El auth-service es una API PÚBLICA — no requiere @SecurityRequirement
 * porque es precisamente el servicio que EMITE los tokens.
 *
 * El caso pedagógico aquí es @ExampleObject: los controllers muestran
 * múltiples ejemplos nombrados de request/response para un mismo endpoint
 * (login como admin, login como user, token expirado, etc.)
 *
 * UI: http://localhost:50003/swagger-ui.html
 * Spec JSON: http://localhost:50003/v3/api-docs
 *
 * Nota: auth-service no tiene context-path, la UI es en la raíz.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Auth Service API")
                                .description("""
                                Servicio de autenticación y autorización del cinema-booking-app MitoCode.

                                **Flujo de autenticación:**
                                1. `POST /api/v1/auth/register` — registrar usuario
                                2. `POST /api/v1/auth/login` — obtener `access_token` y `refresh_token`
                                3. Usar `access_token` como Bearer en los demás servicios
                                4. `POST /api/v1/auth/refresh` — renovar tokens sin re-autenticar
                                
                                **Tokens emitidos:** JWT RS256 firmado con clave RSA privada.
                                Los Resource Servers validan la firma con la clave pública de `GET /oauth2/jwks`.

                                **Usuarios de prueba (en memoria):**
                                | Username | Password   | Rol         |
                                |----------|------------|-------------|
                                | admin    | admin123   | ROLE_ADMIN  |
                                | user     | user123    | ROLE_USER   |
                                """)
                                .version("1.0")
                                .contact(new Contact()
                                        .name("Cinema Api")
                                        .email("desarrollo@mitocode.com")
                                        .url("https://www.mitocode.com/java-spring-boot-course"))
                )
                .addServersItem(new Server()
                        .url("http://localhost:50003")
                        .description("Local - desarrollo"))
                .addServersItem(new Server()
                        .url("https://qa.auth-service.mitocode.com")
                        .description("QA"))
                .addServersItem(new Server()
                        .url("https://auth-service.mitocode.com")
                        .description("Producción")
                );
    }
}
