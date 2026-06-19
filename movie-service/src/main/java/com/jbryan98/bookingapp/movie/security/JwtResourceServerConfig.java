package
com.jbryan98.bookingapp.movie.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class JwtResourceServerConfig {

    /**
     * Convierte los claims del JWT en GrantedAuthority de Spring Security.
     *
     * Mismo comportamiento que JwtResourceServerConfig:
     *   authoritiesClaimName = "roles"  → leer claim "roles" (no el default "scope")
     *   authorityPrefix = ""            → roles ya tienen prefijo "ROLE_"
     *
     * Resultado: JWT con roles=["ROLE_USER"] → GrantedAuthority("ROLE_USER")
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Leer roles del claim "roles"
        grantedAuthoritiesConverter.setAuthorityPrefix(""); // Sin prefijo adicional (las roles ya tienen "

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return authenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter)  throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Actuator accesible sin token (health checks del orquestador de Docker)
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/movies").permitAll()
                        .requestMatchers(HttpMethod.GET, "/movies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/screenings").permitAll()
                        .requestMatchers(HttpMethod.GET, "/screenings/**").permitAll()
                        // Todo lo demás requiere JWT válido
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        // jwk-set-uri se inyecta desde Config Server (application-jwt.yml o
                        // application-keycloak.yml) — no está hardcodeada aquí (OCP: no cambiar código
                        // al cambiar el proveedor de identidad, solo cambiar el perfil activo)
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .build();    }
}
