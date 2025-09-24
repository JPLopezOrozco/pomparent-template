package com.juan.authservice.service.impl;

import com.juan.authservice.dto.LoginRequest;
import com.juan.authservice.dto.RegisterRequest;
import com.juan.authservice.exception.InvalidTokenException;
import com.juan.authservice.exception.LoginException;
import com.juan.authservice.exception.UserAlreadyExistException;
import com.juan.authservice.model.Role;
import com.juan.authservice.model.UserCredential;
import com.juan.authservice.repository.UserCredentialRepository;
import com.juan.authservice.service.IAuthService;
import com.juan.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UserCredential save(RegisterRequest user) {

        String normalized = normalize(user.email());
        if (repository.existsByEmail(normalized)) throw new UserAlreadyExistException("User already exist");

        UserCredential userCredential = UserCredential.builder()
                .name(user.name())
                .email(normalized)
                .password(passwordEncoder.encode(user.password()))
                .role(Role.USER)
                .build();

        try {
            return repository.save(userCredential);
        }catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException("User already exist");
        }

    }
    @Override
    @Transactional(readOnly = true)
    public String login(LoginRequest loginRequest) {
        String normalized = normalize(loginRequest.email());


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        UserCredential user = repository.findByEmail(normalized);
        if (authentication.isAuthenticated()){
            return jwtService.generateToken(normalized, authentication.getAuthorities(), user.getId());
        }else throw new  LoginException("Invalid email or password");

    }

    @Override
    @Transactional(readOnly = true)
    public void validateToken(String token) {
        String normalized = normalize(jwtService.extractUsername(token));
        UserCredential userCredential = repository.findByEmail(normalized);
        if (!jwtService.validateToken(token, userCredential)) throw new InvalidTokenException("Invalid token");
    }

    private String normalize(String email) {
        return Normalizer
                .normalize(email, Normalizer.Form.NFC)
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
