package com.juan.authservice.controller;

import com.juan.authservice.dto.*;
import com.juan.authservice.exception.InvalidTokenException;
import com.juan.authservice.model.UserCredential;
import com.juan.authservice.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {

        UserCredential user = authService.save(registerRequest);
        log.info("User created successfully");

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/auth/{id}")
                .buildAndExpand(user.getId())
                .toUri()
        ).body(RegisterResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){

        log.info("User logged successfully");

        return ResponseEntity.ok(LoginResponse.from(authService.login(loginRequest)));
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new InvalidTokenException("Missing or malformed Authorization header");

        var token = authHeader.substring("Bearer ".length()).trim();
        authService.validateToken(token);

        log.info("Token is valid");

        return ResponseEntity.noContent().build();
    }

}
