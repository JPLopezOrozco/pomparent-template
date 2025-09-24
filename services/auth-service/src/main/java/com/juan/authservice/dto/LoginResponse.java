package com.juan.authservice.dto;

public record LoginResponse(String token) {

    public static LoginResponse from(String token) {
        return new LoginResponse(token);
    }
}
