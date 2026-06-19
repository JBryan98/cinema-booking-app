package com.jbryan98.bookingapp.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Configuración de claves RSA y beans JWT.
 *
 * Genera un par de claves RSA 2048-bit en memoria al arrancar.
 * En producción las claves deberían persistirse (KeyStore, HSM o secrets manager)
 * para que los tokens sigan siendo válidos después de un reinicio.
 *
 * Beans expuestos:
 *   - KeyPair      → par RSA (privada para firmar, pública para verificar)
 *   - JWKSource    → fuente JWK para NimbusJwtEncoder
 *   - JwtEncoder   → firma JWTs con clave RSA privada (RS256)
 *   - JwtDecoder   → verifica JWTs con clave RSA pública
 *   - RSAKey (public) → expuesto como @Bean para el endpoint JWKS
 */
@Configuration
public class JwkConfig {

    /**
     * Genera el par de claves RSA 2048-bit.
     * Se genera una vez al arrancar — todas las firmas de la sesión usan la misma clave.
     */
    @Bean
    public KeyPair rsaKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el par de claves RSA", e);
        }
    }

    /**
     * RSAKey de Nimbus con clave pública y privada.
     * Incluye un keyId (kid) aleatorio para identificar la clave en el JWKS.
     * El kid permite a los Resource Servers seleccionar la clave correcta cuando
     * hay múltiples claves en rotación.
     */
    @Bean
    public RSAKey rsaKey(KeyPair rsaKeyPair) {
        return new RSAKey.Builder((RSAPublicKey) rsaKeyPair.getPublic())
                .privateKey((RSAPrivateKey) rsaKeyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    /**
     * JWKSource: repositorio de claves JWK usado por NimbusJwtEncoder.
     * ImmutableJWKSet → conjunto de claves fijo (sin rotación dinámica).
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * JwtEncoder: firma JWTs con la clave RSA privada usando RS256.
     * Usado en JwtTokenService para generar accessToken y refreshToken.
     */
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    /**
     * JwtDecoder: verifica la firma de JWTs con la clave RSA pública.
     * Usado en JwtTokenService para validar refreshTokens.
     */
    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) {
        try {
            return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo construir el JwtDecoder", e);
        }
    }
}
