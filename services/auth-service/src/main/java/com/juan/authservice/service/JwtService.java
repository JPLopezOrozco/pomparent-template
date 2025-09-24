package com.juan.authservice.service;

import com.juan.authservice.model.UserCredential;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;
    @Value("${jwt.issuer}")
    private String ISSUER;

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities, Long id) {
        Map<String, Object> claims = new HashMap<>();
        List<String> role = (authorities == null) ? List.of():
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                        .distinct()
                        .toList();

        claims.put("roles", role);
        claims.put("id", id);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuer(ISSUER)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .and()
                .signWith(getSignedKey())
                .compact();

    }

    public String extractUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserCredential userCredential) {
        final String username = extractUsername(token);
        return username.equals(userCredential.getEmail()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignedKey() {
        byte [] keyByte = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyByte);
    }

}
