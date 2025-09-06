package com.juan.monolithapp.service.impl;

import com.juan.monolithapp.exception.RefreshTokenException;
import com.juan.monolithapp.jwt.JwtService;
import com.juan.monolithapp.model.RefreshToken;
import com.juan.monolithapp.model.User;
import com.juan.monolithapp.repository.RefreshTokenRepository;
import com.juan.monolithapp.service.IRefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService implements IRefreshToken {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Override
    public RefreshToken issue(User user, String rt) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtService.getKey())
                .build()
                .parseSignedClaims(rt)
                .getPayload();

        Instant now = claims.getIssuedAt().toInstant();
        Instant expiresAt = claims.getExpiration().toInstant();
        String value = claims.get("jti", String.class);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .jti(value)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void revokeAllForUser(Long userId) {
        refreshTokenRepository.revokeAllValidByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateActive(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(jwtService.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String jti = claims.get("jti", String.class);
        if (jti==null) throw new RefreshTokenException("JWT token is missing");

        RefreshToken rt = refreshTokenRepository.findByJtiAndRevokedFalse(jti)
                .orElseThrow(() -> new RefreshTokenException("JWT token not found"));

        if (rt.getExpiresAt().isBefore(Instant.now())) throw new RefreshTokenException("JWT token is expired");

        return rt;
    }

    @Override
    public void revoke(String token) {
        String jti = jwtService.extractJti(token);
        refreshTokenRepository.findByJtiAndRevokedFalse(jti).ifPresent(rt->{rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(Instant.now());
    }
}
