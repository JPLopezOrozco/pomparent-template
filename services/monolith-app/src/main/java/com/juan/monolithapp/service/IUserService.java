package com.juan.monolithapp.service;

import com.juan.monolithapp.dto.RefreshRequestDto;
import com.juan.monolithapp.dto.UserLoginRequestDto;
import com.juan.monolithapp.dto.UserLoginResponseDto;
import com.juan.monolithapp.dto.UserRegisterRequestDto;
import com.juan.monolithapp.model.User;

public interface IUserService {
    User registerUser(UserRegisterRequestDto user);
    UserLoginResponseDto loginUser(UserLoginRequestDto user);
    User findById(Long id);
    UserLoginResponseDto refreshToken(RefreshRequestDto req);
}
