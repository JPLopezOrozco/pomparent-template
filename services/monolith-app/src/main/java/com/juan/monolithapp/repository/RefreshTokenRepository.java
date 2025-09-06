package com.juan.monolithapp.repository;

import com.juan.monolithapp.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByJtiAndRevokedFalse(String token);
    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.user.id = :userId and r.revoked = false")
    void revokeAllValidByUserId(@Param("userId") Long userId);
    @Modifying
    @Query("delete from RefreshToken r where r.expiresAt < :now or r.revoked = true")
    int deleteExpiredTokens(@Param("now") Instant now);
}
