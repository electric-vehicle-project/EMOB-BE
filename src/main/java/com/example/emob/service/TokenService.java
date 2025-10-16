/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.entity.Account;
import com.example.emob.repository.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
  @Autowired AccountRepository accountRepository;

  @Value("${spring.jwt.secretkey}")
  private String SECRET_KEY;

  // tạo reset token
  public String generateResetToken(final Account account) {
    Instant now = Instant.now();
    return Jwts.builder()
        .issuer("your-app") // iss
        .audience()
        .add("password_reset")
        .and() // aud
        .subject(String.valueOf(account.getId())) // sub
        .id(UUID.randomUUID().toString()) // jti
        .claim("token_type", "reset_password") // phân biệt loại token
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(3, java.time.temporal.ChronoUnit.MINUTES))) // hết hạn nhanh
        .signWith(getSignKey(), Jwts.SIG.HS256)
        .compact();
  }

  // verify reset token
  public Account verifyResetToken(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();

      // Kiểm tra loại token
      String type = (String) claims.get("token_type");
      if (!"reset_password".equals(type)) {
        throw new JwtException("Invalid token type");
      }

      // Lấy user id từ subject
      UUID id = UUID.fromString(claims.getSubject());
      return accountRepository.findAccountById(id);

    } catch (ExpiredJwtException e) {
      throw new JwtException("Reset token has expired");
    } catch (JwtException e) {
      throw new JwtException("Invalid or malformed reset token");
    }
  }

  // getSignKey
  public SecretKey getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // verify_token
  public Account verifyToken(String token) {
    Claims claims =
        Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
    String idString = claims.getSubject();
    UUID id = UUID.fromString(idString); // parse sang UUID
    return accountRepository.findAccountById(id);
  }

  // ===== ACCESS TOKEN =====
  public String generateToken(final Account account) {
    Instant now = Instant.now();
    return Jwts.builder()
        .issuer("your-app") // iss
        .audience()
        .add("api")
        .and() // aud
        .subject(String.valueOf(account.getId())) // sub
        .id(UUID.randomUUID().toString()) // jti
        .claim("token_type", "access") // phân biệt loại
        .claim("roles", "ROLE_" + account.getRole()) // roles nếu bạn cần
        .issuedAt(Date.from(now)) // iat
        .expiration(Date.from(now.plus(15, java.time.temporal.ChronoUnit.MINUTES))) // 15'
        .signWith(getSignKey(), Jwts.SIG.HS256) // KHÔNG dùng chung key với refresh
        .compact();
  }
}
