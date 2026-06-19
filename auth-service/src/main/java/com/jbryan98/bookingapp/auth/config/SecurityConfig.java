package com.jbryan98.bookingapp.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Configuración de seguridad del auth-service.
 *
 * Política de acceso:
 *   - /api/v1/auth/**   → público (login, register, refresh)
 *   - /oauth2/**         → público (callback GitHub, JWKS)
 *   - /actuator/health  → público (healthcheck Docker)
 *   - resto              → requiere autenticación (protección por defecto)
 *
 * El auth-service NO es un Resource Server — no valida tokens JWT entrantes.
 * Es el EMISOR de tokens: recibe username/password y devuelve JWT.
 *
 * STATELESS: no se crean sesiones HTTP. Cada request es independiente.
 * CSRF deshabilitado: APIs REST sin estado no necesitan protección CSRF.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RSAKey rsaKey;
    private final JWKSet jwkSet;

    public SecurityConfig(RSAKey rsaKey) {
        this.rsaKey = rsaKey;
        this.jwkSet = new JWKSet(rsaKey);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        // Swagger UI - Sin context-path en auth-service
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * AuthenticationManager expuesto como Bean.
     * Necesario para que AuthController pueda llamar a authenticate()
     * sin extender WebSecurityConfigurerAdapter (eliminado en Spring Security 6).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Endpoint JWKS: expone la clave pública RSA en formato JWK Set.
     *
     * GET /oauth2/jwks
     * Respuesta: { "keys": [{ "kty": "RSA", "e": "AQAB", "n": "...", "kid": "..." }] }
     *
     * Los Resource Servers (api-gateway, order-orchestrator, order-service) llaman
     * a este endpoint al arrancar para descargar la clave pública y validar JWTs
     * localmente sin hacer una llamada HTTP por cada request.
     *
     * Implementado como inner @RestController dentro de la configuración de seguridad
     * para mantener cohesión: la clave RSA ya está disponible aquí.
     */
    @RestController
    class JwksController {

        @GetMapping(value = "/oauth2/jwks", produces = MediaType.APPLICATION_JSON_VALUE)
        public Map<String, Object> jwks() {
            // toPublicJWKSet() → solo expone claves públicas (nunca la privada)
            // toJSONObject()   → { "keys": [{ "kty": "RSA", "e": "AQAB", "n": "...", "kid": "..." }] }
            // Este es el formato estándar JWK Set que espera spring-security oauth2ResourceServer
            return jwkSet.toPublicJWKSet().toJSONObject();
        }
    }
}
