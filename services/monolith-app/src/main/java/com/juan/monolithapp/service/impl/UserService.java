package com.juan.monolithapp.service.impl;

import com.juan.monolithapp.dto.RefreshRequestDto;
import com.juan.monolithapp.dto.UserLoginRequestDto;
import com.juan.monolithapp.dto.UserLoginResponseDto;
import com.juan.monolithapp.dto.UserRegisterRequestDto;
import com.juan.monolithapp.exception.UserNotfoundException;
import com.juan.monolithapp.jwt.JwtService;
import com.juan.monolithapp.model.RefreshToken;
import com.juan.monolithapp.model.Role;
import com.juan.monolithapp.model.User;
import com.juan.monolithapp.repository.UserRepository;
import com.juan.monolithapp.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public User registerUser(UserRegisterRequestDto user) {
        User userSaved = User.builder()
                .name(user.name())
                .email(user.email())
                .password(passwordEncoder.encode(user.password()))
                .role(Role.USER)
                .build();
        return userRepository.save(userSaved);
    }

    @Override
    public UserLoginResponseDto loginUser(UserLoginRequestDto user) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(user.email(), user.password())
                    );
            if (authentication.isAuthenticated()) {
                User userWanted = userRepository.findByEmail(user.email());

                refreshTokenService.revokeAllForUser(userWanted.getId());

                String access = jwtService.generateToken(user.email());
                String refresh = jwtService.refreshToken(user.email());

                refreshTokenService.issue(userWanted, refresh);

                return UserLoginResponseDto.builder()
                        .token(access)
                        .refreshToken(refresh)
                        .build();
            }else {
                throw new BadCredentialsException("Invalid username or password");
            }
        }
        catch (Exception e) {
            throw new BadCredentialsException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotfoundException("User with id " + id + " not found"));
    }

    @Override
    public UserLoginResponseDto refreshToken(RefreshRequestDto req) {
        RefreshToken active = refreshTokenService.validateActive(req.refreshToken());

        refreshTokenService.revoke(req.refreshToken());

        String access = jwtService.generateToken(active.getUser().getEmail());
        String refresh = jwtService.refreshToken(active.getUser().getEmail());
        refreshTokenService.issue(active.getUser(), refresh);
        return new UserLoginResponseDto(access, refresh);
    }

}
