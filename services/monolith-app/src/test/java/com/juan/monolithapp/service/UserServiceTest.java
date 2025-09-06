package com.juan.monolithapp.service;

import com.juan.monolithapp.dto.UserLoginRequestDto;
import com.juan.monolithapp.dto.UserLoginResponseDto;
import com.juan.monolithapp.dto.UserRegisterRequestDto;
import com.juan.monolithapp.jwt.JwtService;
import com.juan.monolithapp.model.RefreshToken;
import com.juan.monolithapp.model.User;
import com.juan.monolithapp.repository.UserRepository;
import com.juan.monolithapp.service.impl.RefreshTokenService;
import com.juan.monolithapp.service.impl.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Test
    public void findByIdTest(){
        User user = User.builder()
                .id(3L)
                .email("test@test.com")
                .name("Test")
                .password("test")
                .build();

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        User userFound = userService.findById(3L);

        Assertions.assertThat(userFound).isEqualTo(user);
        Assertions.assertThat(userFound.getEmail()).isEqualTo("test@test.com");
        Assertions.assertThat(userFound.getName()).isEqualTo("Test");
        Mockito.verify(userRepository, Mockito.times(1)).findById(3L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void registerUserTest(){
        UserRegisterRequestDto user = UserRegisterRequestDto.builder()
                .email("test@test.com")
                .name("Test")
                .password(passwordEncoder.encode("test"))
                .build();

        when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv->{
            User u = inv.getArgument(0);
            u.setId(5L);
            return u;
        });

        User saved = userService.registerUser(user);

        Assertions.assertThat(saved.getId()).isEqualTo(5L);
        Assertions.assertThat(saved.getEmail()).isEqualTo("test@test.com");
        Assertions.assertThat(saved.getName()).isEqualTo("Test");
        Assertions.assertThat(saved.getPassword()).isNotEqualTo("test");
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode("test");
        Mockito.verifyNoMoreInteractions(userRepository);

    }


    @Test
    public void loginUserTest(){

        UserLoginRequestDto userRq = new  UserLoginRequestDto("test@test.com", "test");
        User user = User.builder()
                .id(5L)
                .email("test@test.com")
                .name(passwordEncoder.encode("Test"))
                .password(passwordEncoder.encode("test"))
                .build();


        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userRq.email(),
                userRq.password(),
                List.of(new SimpleGrantedAuthority("USER"))
        );

        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);

        doNothing().when(refreshTokenService).revokeAllForUser(5L);

        when(jwtService.generateToken(Mockito.anyString())).thenReturn("access.token");
        when(jwtService.refreshToken(Mockito.anyString())).thenReturn("refresh.token");

        when(refreshTokenService.issue(eq(user), eq("refresh.token")))
                .thenReturn(RefreshToken.builder()
                        .id(1L)
                        .user(user)
                        .jti("jti")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(60))
                        .revoked(false)
                        .build());

        UserLoginResponseDto userRs = userService.loginUser(userRq);
        Assertions.assertThat(userRs).isNotNull();
        Assertions.assertThat(userRs.token()).isEqualTo("access.token");
        Assertions.assertThat(userRs.refreshToken()).isEqualTo("refresh.token");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, Mockito.times(1)).findByEmail("test@test.com");
        verify(jwtService).generateToken(Mockito.anyString());
        verify(jwtService).refreshToken(Mockito.anyString());
        verify(refreshTokenService).revokeAllForUser(5L);
        verify(refreshTokenService).issue(eq(user), eq("refresh.token"));
        verifyNoMoreInteractions(userRepository, authenticationManager, jwtService, refreshTokenService);

    }


}
