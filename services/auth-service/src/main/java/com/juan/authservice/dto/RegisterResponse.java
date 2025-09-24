package com.juan.authservice.dto;

import com.juan.authservice.model.UserCredential;

public record RegisterResponse(String name, String email, String password) {

    public static RegisterResponse from(UserCredential userCredential) {
        return new RegisterResponse(
                userCredential.getName(),
                userCredential.getEmail(),
                userCredential.getPassword()
        );
    }


}
