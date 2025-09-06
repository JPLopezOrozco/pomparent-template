package com.juan.monolithapp.controller;

import com.juan.monolithapp.dto.*;
import com.juan.monolithapp.jwt.JwtService;
import com.juan.monolithapp.model.RefreshToken;
import com.juan.monolithapp.model.User;
import com.juan.monolithapp.model.UserPrincipal;
import com.juan.monolithapp.service.IUserService;
import com.juan.monolithapp.service.impl.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/{id}")
    public ResponseEntity<UserRegisterResponseDto> userById(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok().body(UserRegisterResponseDto.from(user));
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDto> register(@Valid @RequestBody UserRegisterRequestDto userRegisterRequestDto) {
        User user = userService.registerUser(userRegisterRequestDto);

        var location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/auth/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(UserRegisterResponseDto.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto) {
        UserLoginResponseDto user = userService.loginUser(userLoginRequestDto);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserLoginResponseDto> refresh(@Valid @RequestBody RefreshRequestDto req) {
        UserLoginResponseDto response = userService.refreshToken(req);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserPrincipal user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        refreshTokenService.revokeAllForUser(user.getUser().getId());
        return ResponseEntity.ok().body("User logged out successfully");
    }

}
