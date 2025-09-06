package com.juan.monolithapp.dto;

import com.juan.monolithapp.model.Role;
import com.juan.monolithapp.model.User;

public record UserRegisterResponseDto(String email, String name, Role role) {

    public static UserRegisterResponseDto from(User user) {
        return new UserRegisterResponseDto(
                user.getEmail(),
                user.getName(),
                user.getRole());
    }

}
