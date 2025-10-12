/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.entity.Account;
import com.example.emob.entity.RefreshToken;
import com.example.emob.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

  @Autowired RefreshTokenRepository refreshTokenRepository;

  @Value("${spring.jwt.duration}")
  private long DURATION;

  public RefreshToken createRefreshToken(Account account) {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setToken(UUID.randomUUID() + UUID.randomUUID().toString());
    refreshToken.setAccountId(account.getId().toString());
    refreshToken.setIssuedAt(Instant.now());
    refreshToken.setRevoked(false);
    refreshToken.setTtl(DURATION * 24 * 60 * 60);

    return refreshTokenRepository.save(refreshToken);
  }

  public Optional<RefreshToken> verifyToken(String token) {
    return refreshTokenRepository.findById(token).filter(rt -> !rt.isRevoked());
  }

  public void revokeToken(String token) {
    refreshTokenRepository
        .findById(token)
        .ifPresent(
            rt -> {
              rt.setRevoked(true);
              refreshTokenRepository.save(rt);
            });
  }

  public void revokeAllTokens(String accountId) {
    List<RefreshToken> tokens = refreshTokenRepository.findAllByAccountId(accountId);
    tokens.forEach(rt -> rt.setRevoked(true));
    refreshTokenRepository.saveAll(tokens);
  }

  public RefreshToken rotateToken(String oldToken, Account account) {
    // 1. revoke token cũ
    refreshTokenRepository
        .findById(oldToken)
        .ifPresent(
            rt -> {
              rt.setRevoked(true);
              refreshTokenRepository.save(rt);
            });

    return createRefreshToken(account);
  }
}
