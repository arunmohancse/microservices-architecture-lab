package com.lab.auth.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * Generates an RSA key pair on startup and exposes beans for:
 *  - JWT encoding  (NimbusJwtEncoder — used in JwtService)
 *  - JWKS endpoint (JWKSet — served at /.well-known/jwks.json)
 *
 * NOTE: The key is re-generated on every restart in this phase.
 *       Existing tokens become invalid after a restart — acceptable for Phase 1.
 *       Phase 9 (Config Server) will load a persistent key from a secret store.
 */
@Configuration
public class JwtConfig {

    @Bean
    public RSAKey rsaKey() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var keyPair = generator.generateKeyPair();

        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();
    }

    /**
     * Public-key-only JWKSet — safe to expose at the JWKS endpoint.
     * Resource servers (product-service, order-service) use this to validate tokens.
     */
    @Bean
    public JWKSet jwkSet(RSAKey rsaKey) {
        return new JWKSet(rsaKey.toPublicJWK());
    }

    /**
     * Full (private) JWKSource — used internally by NimbusJwtEncoder to sign tokens.
     */
    @Bean
    public NimbusJwtEncoder jwtEncoder(RSAKey rsaKey) throws KeySourceException {
        ImmutableJWKSet<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }
}
