package com.juan.authservice.repository;

import com.juan.authservice.model.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    UserCredential findByEmail(String email);
    boolean existsByEmail(String email);
}
