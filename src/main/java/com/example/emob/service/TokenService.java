package com.example.emob.service;

import com.example.emob.entity.Account;
import com.example.emob.repository.AccountRepository;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

@Service
public class TokenService {
    @Autowired
    AccountRepository accountRepository;
    protected final String SECRET_KEY = "yxzHhwvD3gfwENyU95c1VUb2iH5manykVOuPwj1WTy4=";

    // getSignKey
    public SecretKey getSignKey () {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // verify_token
    public Account verifyToken (String token) {
        Claims claims = Jwts.parser().verifyWith(getSignKey()).
                build().
                parseSignedClaims(token).
                getPayload();
        String idString = claims.getSubject();
        long id = Long.parseLong(idString);
        return accountRepository.findAccountById(id);
    }
}
