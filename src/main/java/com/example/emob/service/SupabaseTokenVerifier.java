package com.example.emob.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.example.emob.constant.ErrorCode;
import com.example.emob.exception.GlobalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class SupabaseTokenVerifier {
    @Value("${supabase.jwt.secret}")
    private String supabaseJwtSecret;
    public String verifySupabaseToken(String token) {
        try {

            // Tạo thuật toán xác minh HMAC SHA256 với Supabase secret
            Algorithm algorithm = Algorithm.HMAC256(supabaseJwtSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://gffjbqzhcrykuzfjsxim.supabase.co/auth/v1")
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("email").asString();
        } catch (Exception e) {
            System.out.println("❌ Token không hợp lệ: " + e.getMessage());
            throw new GlobalException(ErrorCode.UNAUTHENTICATED, "Invalid Supabase token");
        }
    }
}
