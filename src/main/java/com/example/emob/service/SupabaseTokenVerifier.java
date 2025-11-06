package com.example.emob.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.stereotype.Service;

import java.net.URL;
@Service
public class SupabaseTokenVerifier {
    private static final String SUPABASE_JWKS_URL =
            "https://gffjbqzhcrykuzfjsxim.supabase.co/auth/v1/jwks";

    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
    public SupabaseTokenVerifier() throws Exception {
        JWKSource<SecurityContext> keySource =
                new RemoteJWKSet<>(new URL(SUPABASE_JWKS_URL));

        jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
    }

    public String getEmailFromToken(String token) {
        try {
            var claims = jwtProcessor.process(SignedJWT.parse(token), null);
            return claims.getStringClaim("email");
        } catch (Exception e) {
            System.err.println("Invalid token: " + e.getMessage());
            return null;
        }
    }
}
