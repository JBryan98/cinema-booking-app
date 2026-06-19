package com.jbryan98.bookingapp.auth.application;



import com.jbryan98.bookingapp.auth.api.dto.RegisterRequest;
import com.jbryan98.bookingapp.auth.api.dto.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

/**
 * Servicio de generación y validación de JWT.
 *
 * Responsabilidades:
 *   - Firmar accessToken con clave RSA privada (RS256) vía JwtEncoder
 *   - Firmar refreshToken (misma clave, TTL mayor, claim "token_use=refresh")
 *   - Validar refreshToken y emitir nuevo par de tokens
 *   - Registrar usuarios en InMemoryUserDetailsManager
 *
 * Claims del accessToken:
 *   iss  → issuer del servicio (http://localhost:50003)
 *   sub  → username
 *   iat  → emitido en
 *   exp  → expira en (iat + accessTokenTtl)
 *   roles → lista de roles Spring Security (["ROLE_ADMIN"])
 *
 * Claims adicionales del refreshToken:
 *   token_use → "refresh" (distingue del accessToken)
 *   exp        → iat + refreshTokenTtl (más largo)
 */
@Service
public class JwtTokenService {

    private final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.jwt.issuer:http://localhost:50003}")
    private String issuer;

    @Value("${auth.jwt.access-token-ttl:3600}")
    private long accessTokenTtl;

    @Value("${auth.jwt.refresh-token-ttl:86400}")
    private long refreshTokenTtl;

    public JwtTokenService(JwtEncoder jwtEncoder,
                           JwtDecoder jwtDecoder,
                           InMemoryUserDetailsManager userDetailsManager,
                           PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Genera un par accessToken + refreshToken para la autenticación dada.
     * Llamado tras autenticación exitosa con username/password.
     */
    public TokenResponse generateTokens(Authentication authentication) {
        List<String> roles =authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String accessToken = buildAccessToken(authentication.getName(), roles);
        String refreshToken = buildRefreshToken(authentication.getName(), roles);

        return TokenResponse.bearer(accessToken, refreshToken, accessTokenTtl);
    }

    /**
     * Genera un par accessToken + refreshToken para un usuario autenticado con GitHub OAuth2.
     *
     * El "sub" del JWT usa el "login" de GitHub (username único, ej: "octocat").
     * El rol asignado es ROLE_USER por defecto — los usuarios externos no son admins.
     *
     * Para conceder ROLE_ADMIN a usuarios específicos de GitHub, comparar
     * oauth2User.getAttribute("login") contra una lista de admins configurada.
     *
     * @param oauth2User perfil del usuario obtenido de la GitHub User API
     */
    public TokenResponse generateTokensFromGitHub(OAuth2User oauth2User) {
        String username = oauth2User.getAttribute("login");

        if (username == null || StringUtils.hasText(username)) {
            // Fallback por si GitHub no devuelve "login" (no debería ocurrir)
            Object id = oauth2User.getAttribute("id");
            username = "github-user-" + id;
        }

        // Por defecto, usuarios externos de GitHub obtienen ROLE_USER
        List<String> roles = List.of("ROLE_USER");

        String accessToken = buildAccessToken(username, roles);
        String refreshToken = buildRefreshToken(username, roles);

        return TokenResponse.bearer(accessToken, refreshToken, accessTokenTtl);
    }

    /**
     * Valida el refreshToken y emite un nuevo par de tokens.
     * Verifica: firma RSA, expiración, claim token_use=refresh.
     */
    public TokenResponse refreshTokens(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);

        String tokenUse = jwt.getClaimAsString("token_use");

        if (!"refresh".equals(tokenUse)) {
            throw new JwtException("Token inválido: no es un refresh token");
        }

        String username = jwt.getSubject();

        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String newAccessToken = buildAccessToken(username, roles);
        String newRefreshToken = buildRefreshToken(username, roles);

        return TokenResponse.bearer(newAccessToken, newRefreshToken, accessTokenTtl);
    }

    /**
     * Registra un nuevo usuario en el InMemoryUserDetailsManager.
     * El role se normaliza: si no tiene prefijo ROLE_ se añade automáticamente.
     */
    public void register(RegisterRequest request) {

        String password = passwordEncoder.encode(request.password());
        log.info("====================================================");
        log.info("PASSWORD: {} → {}", request.password(), password);
        UserDetails newUser = User.builder()
                .username(request.username())
                .password(password)
                .roles(request.role())  // User.Builder.roles() añade ROLE_ automáticamente
                .build();

        userDetailsManager.createUser(newUser);
    }

    // ── Métodos privados de construcción de tokens ────────────────────────────

    private String buildAccessToken(String username, List<String> roles) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenTtl))
                .claim("roles", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String buildRefreshToken(String username, List<String> roles) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenTtl))
                .claim("roles", roles)
                .claim("token_use", "refresh")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
