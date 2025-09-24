package com.juan.authservice.service;

import com.juan.authservice.dto.LoginRequest;
import com.juan.authservice.dto.RegisterRequest;
import com.juan.authservice.model.UserCredential;

public interface IAuthService {
    UserCredential save(RegisterRequest user);
    String login(LoginRequest user);
    void validateToken(String token);
}
